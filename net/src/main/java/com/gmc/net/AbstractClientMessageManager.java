package com.gmc.net;

import com.gmc.config.ConfigProperty;
import com.gmc.core.Tuple2;

import java.util.Map;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

public abstract class AbstractClientMessageManager extends AbstractLifeCycle implements ClientMessageManager {
    private Function<Message, Long> getMessageIdFunction;
    private BiConsumer<Message, Long> setMessageIdFunction;
    private Map<Long, MessageWrap> messageIdWrapMap = new ConcurrentHashMap<>();
    private BlockingDeque<MessageWrap> sendMessages = new LinkedBlockingDeque<>();
    private BlockingDeque<MessageWrap> needReceiveResponse = new LinkedBlockingDeque<>();
    private AtomicLong messageIdGenerate = new AtomicLong();
    private AtomicInteger notFlushMessageCount = new AtomicInteger();
    //message may not flush or have flushed but not receive
    private AtomicInteger notReceiveMessageCount = new AtomicInteger();
    private boolean messageIdAutoIncrease;
    private CloseableScheduledExecutor executor;
    private Client client;
    private Client.Channel channel;
    private boolean batch = false;
    private int retry;
    WriteMessageTask task = new WriteMessageTask();
    private ClientConnectManager clientConnectManager;
    private int flushTimeInterval;
    private int flushCountInterval;
    private Timeout flushTimeout;

    public AbstractClientMessageManager(Client client, Function<Message, Long> getMessageIdFunction, BiConsumer<Message, Long> setMessageIdFunction) {
        this.client = client;
        this.channel = client.getChannel();
        this.executor = client.getExecutor();
        ConfigProperty configProperty = client.getConfig();
        messageIdAutoIncrease = configProperty.getConfig(ClientConfig.MESSAGE_ID_AUTO_INCREASE);
        batch = configProperty.getConfig(ClientConfig.MESSAGE_SEND_BATCH);
        flushTimeInterval = configProperty.getConfig(ClientConfig.FLUSH_TIME_INTERVAL);
        flushTimeout = new Timeout(TimeUnit.MILLISECONDS, flushTimeInterval);
        flushCountInterval = configProperty.getConfig(ClientConfig.FLUSH_COUNT_INTERVAL);
        this.getMessageIdFunction = getMessageIdFunction;
        this.setMessageIdFunction = setMessageIdFunction;
        this.clientConnectManager = client.getClientConnectManager();

    }

    private void reWrite(MessageWrap messageWrap) {
        if (messageWrap.retry()) {
            messageWrap.decreaseRetry();
            invokeWrite();
        }

    }

    @Override
    public void start() {
        if (batch) {
            executor.schedule(new Runnable() {
                @Override
                public void run() {
                    channel.flush();
                    executor.schedule(this, flushTimeout.getTimeoutInMilli(), TimeUnit.MILLISECONDS);
                }
            }, flushTimeout.getTimeoutInMilli(), TimeUnit.MILLISECONDS);
        }
    }

    private long setMessageId(Message message) {
        long messageId = -1;
        if (messageIdAutoIncrease) {
            messageId = messageIdGenerate.getAndIncrement();
            setMessageIdFunction.accept(message, messageId);
        } else {
            messageId = getMessageIdFunction.apply(message);
        }
        return messageId;
    }

    protected void processMessageWrap(MessageWrap messageWrap) {

    }

    @Override
    public long putMessage(Message message, Client.SendNotify sendNotify, Client.ReceiveNotify receiveNotify) {
        ExceptionUtils.checkNotNull(message, "message must not null");
        if (sendNotify == null) {
            sendNotify = Client.SendNotify.DO_NOTHING;
        }
        if (receiveNotify == null) {
            receiveNotify = Client.ReceiveNotify.DO_NOTHING;
        }
        long messageId = setMessageId(message);
        MessageWrap messageWrap = new MessageWrap(message, sendNotify, receiveNotify, retry);
        processMessageWrap(messageWrap);
        messageIdWrapMap.put(messageId, messageWrap);
        sendMessages.add(messageWrap);
        notFlushMessageCount.incrementAndGet();
        notReceiveMessageCount.incrementAndGet();
        invokeWrite();
        return messageId;
    }

    public void invokeWrite() {

        executor.execute(task);
    }

    private MessageWrap getMessageWrap(long messageId) {
        return messageIdWrapMap.get(messageId);
    }

    class WriteMessageTask implements Runnable {

        @Override
        public void run() {
            MessageWrap poll = sendMessages.poll();
            if (poll != null) {
                CompletableFuture<Void> write = channel.write(poll.getMessage());
                if (!batch || notFlushMessageCount.get() >= flushCountInterval) {
                    flushTimeout.forceBegin();
                    channel.flush();

                }
                write.handleAsync(new BiFunction<Void, Throwable, Void>() {

                    @Override
                    public Void apply(Void unused, Throwable throwable) {
                        if (throwable != null) {
                            if (throwable.getClass().equals(ConnectFailException.class)) {
                                if (poll.retry()) {
                                    sendMessages.addFirst(poll);
                                    clientConnectManager.addConnectListener((channel, epoch) -> {
                                        reWrite(poll);
                                    });
                                } else {
                                    notFlushMessageCount.decrementAndGet();
                                    poll.getSendNotify().onException(new MaxRetryException(retry, throwable.getCause()));
                                }
                            } else if (throwable.getClass().equals(CancellationException.class)) {
                                //discard message
                                notFlushMessageCount.decrementAndGet();

                                poll.getSendNotify().onException(throwable);

                            } else {
                                //some other error,  discard message
                                notFlushMessageCount.decrementAndGet();
                                poll.getSendNotify().onException(throwable);

                            }
                        } else {
                            notFlushMessageCount.decrementAndGet();
                            poll.getSendNotify().hasSent();
                            needReceiveResponse.add(poll);
                        }
                        return null;
                    }
                }, executor);
            }
        }
    }


    @Override
    public void receiveMessage(Message message) {
        executor.execute(() -> {
            notReceiveMessageCount.getAndDecrement();
            Long messageId = getMessageIdFunction.apply(message);
            MessageWrap messageWrap = messageIdWrapMap.remove(messageId);
            messageWrap.getReceiveNotify().receive(message);
            messageWrap.markProcessed();
            removeProcessedMessageWrap();
        });
    }

    private void removeProcessedMessageWrap() {
        while (true) {
            MessageWrap messageWrap = needReceiveResponse.peekFirst();
            if (messageWrap.hasProcessed()) {
                sendMessages.pollFirst();
            } else {
                break;
            }
        }
    }

    @Override
    protected void close0() {
        MessageWrap poll = sendMessages.poll();
        while (poll != null) {
            poll.getSendNotify().onException(new CloseException());
            poll = sendMessages.poll();
        }
        poll = needReceiveResponse.poll();
        while (poll != null) {
            poll.getReceiveNotify().onException(new CloseException());
        }
        messageIdWrapMap.clear();

    }

    class SendNotifyWrap implements Client.SendNotify {
        Client.SendNotify sendNotify;

        @Override
        public void hasSent() {
            if (hasSendHook()) {
                sendNotify.hasSent();
            }
        }

        boolean hasSendHook() {
            return true;
        }

        @Override
        public void onException(Throwable throwable) {
            throwable = onExceptionHook(throwable);
            if (throwable != null) {
                sendNotify.onException(throwable);
            }
        }

        Throwable onExceptionHook(Throwable throwable) {
            return throwable;
        }
    }
}
