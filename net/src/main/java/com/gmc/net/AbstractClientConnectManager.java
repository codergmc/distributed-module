package com.gmc.net;

import com.gmc.core.AtomicUtils;
import com.gmc.core.Tuple2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class AbstractClientConnectManager extends AbstractLifeCycle implements ClientConnectManager {
    public static final Logger LOGGER = LoggerFactory.getLogger(AbstractClientConnectManager.class);
    Client client;
    Client.Channel channel;
    Timeout connectTimeout;
    boolean autoReconnect;
    Executor executor;
    volatile InetSocketAddress remote;
    AtomicInteger channelEpoch = new AtomicInteger();

    public AbstractClientConnectManager(Client client) {
        this.connectTimeout = new Timeout(TimeUnit.MILLISECONDS, client.getConfig().getConfig(ClientConfig.CONNECT_WAIT_TIME_OUT));
        this.autoReconnect = client.getConfig().getConfig(ClientConfig.AUTO_RECONNECT);
        this.executor = client.getExecutor();
        this.client = client;
        this.channel = client.getChannel();
    }

    @Override
    public void start() {
        client.getChannel().addDisConnectListener(new DisConnectListener(), 1);
        client.getChannel().addConnectListener(new ConnectListener(), 1);
    }

    class DisConnectListener implements Client.Channel.Listener {

        @Override
        public void notify(Client.Channel channel, int epoch) {
            executor.execute(() -> {
                try {
                    channel.addConnectListener(new ConnectListener(), epoch + 1);
                    connectTimeout.reset();
                    AbstractClientConnectManager.this.reconnect();
                } catch (Exception e) {
                    LOGGER.error("", e);
                    try {
                        client.close();
                    } catch (Exception exception) {
                        LOGGER.error("", e);
                    }
                }
            });
        }
    }

    class ConnectListener implements Client.Channel.Listener {

        @Override
        public void notify(Client.Channel channel, int epoch) {
            executor.execute(() -> {
                AtomicUtils.set(channelEpoch, oldEpoch -> oldEpoch < epoch
                        , oldEpoch -> epoch);
                channel.addDisConnectListener(new DisConnectListener(), epoch);
                connectTimeout.reset();


            });

        }
    }

    @Override
    protected void close0() {
        try {
            client.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void reconnect() throws ConnectTimeoutException {
        if (autoReconnect) {
            connectTimeout.forceBegin();
            while (!connectTimeout.timeout()) {
                CompletableFuture<Tuple2<Integer, Client.Channel>> tuple2 = channel.connect(remote, channelEpoch.get());
                long remainTime = connectTimeout.remainTimeInMillis();
                if (remainTime > 0) {
                    try {
                        tuple2.get(remainTime, TimeUnit.MILLISECONDS);
                        return;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        break;
                    } catch (ExecutionException | TimeoutException e) {
                        continue;

                    }
                }
            }
        }
        throw new ConnectTimeoutException();
    }

    public void addConnectListener(Client.Channel.Listener listener) {
        channel.addConnectListener(listener, channelEpoch.get());
    }

    @Override
    public CompletableFuture<Void> connect(InetSocketAddress remote) {
        if (channel.isConnected()) {
            CompletableFuture<Void> result = new CompletableFuture<>();
            result.complete(null);
            return result;
        }
        this.remote = remote;
        int epoch = channelEpoch.get();
        CompletableFuture<Tuple2<Integer, Client.Channel>> connect = channel.connect(remote, epoch);
        return connect.handleAsync((tuple2, throwable) -> {
            if (throwable == null) {
                AtomicUtils.set(channelEpoch, oldEpoch -> oldEpoch < tuple2
                        .getV1(), oldEpoch -> tuple2.getV1());
                return null;
            } else {
                try {
                    reconnect();
                    return null;
                } catch (ConnectTimeoutException e) {
                    throw new RuntimeException(e);
                }
            }

        }, executor);
    }
}
