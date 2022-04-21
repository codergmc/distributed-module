package com.gmc.net;

import com.gmc.core.AtomicUtils;
import com.gmc.core.LogUtils;
import com.gmc.core.Tuple2;
import com.gmc.core.Tuple3;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.stream.FactoryConfigurationError;
import java.net.InetSocketAddress;
import java.nio.channels.ClosedChannelException;
import java.util.Map;
import java.util.NavigableMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicReference;

public class NettyClientChannel implements Client.Channel {
    static final Logger LOGGER = LoggerFactory.getLogger(NettyClientChannel.class);
    Bootstrap bootstrap = new Bootstrap();
    NioEventLoopGroup eventLoopGroup;

    // epoch,state,channel
    AtomicReference<Tuple3<Integer, State, Channel>> channelAndEpochState = new AtomicReference<>();
    EpochListenersMap connectListeners = new EpochListenersMap();
    EpochListenersMap disconnectListeners = new EpochListenersMap();
    NettyClient nettyClient;
    NettyMessageCodeManager nettyMessageCodeManager;
    AbstractNettyClientHeartbeatManager nettyClientHeartbeatManager;
    AbstractNettyClientMessageManager nettyClientMessageManager;
    AbstractNettyClientConnectManager nettyClientConnectManager;

    public NettyClientChannel(NettyClient nettyClient) {
        this.nettyClient = nettyClient;
        channelAndEpochState.compareAndSet(null, Tuple3.of(0, State.NEW, null));
    }

    public NettyClientChannel() {
    }

    private boolean transformState(State newState, int oldEpoch, int newEpoch) {
        return AtomicUtils.set(channelAndEpochState, tuple3 -> tuple3.getV2().canTransform(newState) && tuple3.getV1() == oldEpoch, tuple3 -> Tuple3.of(newEpoch, newState, tuple3.getV3()));
    }

    private boolean transformState(State newState, int oldEpoch, int newEpoch, io.netty.channel.Channel newChannel) {
        return AtomicUtils.set(channelAndEpochState, tuple3 -> tuple3.getV2().canTransform(newState) && tuple3.getV1() == oldEpoch, tuple3 -> Tuple3.of(newEpoch, newState, newChannel));
    }

    @Override
    public void init() {
        nettyClientConnectManager = (AbstractNettyClientConnectManager) nettyClient.getClientConnectManager();
        nettyClientMessageManager = (AbstractNettyClientMessageManager) nettyClient.getClientMessageManager();
        nettyClientHeartbeatManager = (AbstractNettyClientHeartbeatManager) nettyClient.getClientHeartbeatManager();
        nettyMessageCodeManager = (NettyMessageCodeManager) nettyClient.getMessageCodeManager();
        if (transformState(State.INIT, 0, 0)) {
            eventLoopGroup = new NioEventLoopGroup(1);
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.group(eventLoopGroup);
            //todo
            bootstrap.option(ChannelOption.TCP_NODELAY, true);
            bootstrap.option(ChannelOption.SO_LINGER, 0);
            bootstrap.handler(new ChannelInitializer<>() {
                @Override
                protected void initChannel(io.netty.channel.Channel ch) throws Exception {

                    nettyClientHeartbeatManager.nettyInit(ch);

                    nettyClientConnectManager.nettyInit(ch);

                    nettyMessageCodeManager.nettyInit(ch);

                    nettyClientMessageManager.nettyInit(ch);

                    ch.pipeline().addLast(NettyUtils.getNettyExceptionHandler());


                }

            });
        }

    }


    @Override
    public CompletableFuture<Void> write(Message message) {
        CompletableFuture<Void> completableFuture = new CompletableFuture<>();
        Tuple3<Integer, State, io.netty.channel.Channel> tuple3 = channelAndEpochState.get();
        io.netty.channel.Channel now = tuple3.getV3();
        if (tuple3.getV2().equals(State.CONNECTED) && now != null) {
            now.write(message).addListener(future -> {
                if (future.isSuccess()) {
                    completableFuture.complete(null);
                } else {
                    if (future.isCancelled()) {
                        completableFuture.completeExceptionally(new CancellationException());
                    } else {
                        if (future.cause().getClass().equals(ClosedChannelException.class)) {
                            completableFuture.completeExceptionally(new ConnectFailException());
                        } else {
                            completableFuture.completeExceptionally(future.cause());
                        }

                    }
                }
            });
        } else {
            completableFuture.completeExceptionally(new ConnectFailException());
        }
        return completableFuture;


    }

    private void closeEventLoop() {
        eventLoopGroup.shutdownGracefully();
    }

    /**
     * new channel that epoch equal param {@code epoch} connect success
     *
     * @param epoch
     */
    void notifyConnectListener(int epoch) {
        connectListeners.notifyListener(epoch, true);
    }

    /**
     * channel ( <= {@code epoch}) is closed
     *
     * @param epoch
     */
    void notifyDisConnectListener(int epoch) {
        disconnectListeners.notifyListener(epoch, false);
    }

    void connect0(InetSocketAddress address, CompletableFuture<Tuple2<Integer, Client.Channel>> completableFuture, int epoch) {
        ChannelFuture channelFuture = bootstrap.connect(address.getAddress(), address.getPort());
        channelFuture.addListener((ChannelFutureListener) future -> {
            Tuple3<Integer, State, io.netty.channel.Channel> tuple3 = channelAndEpochState.get();
            if (future.isSuccess()) {
                if (transformState(State.CONNECTED, epoch, epoch + 1, future.channel())) {
                    completableFuture.complete(Tuple2.of(epoch + 1, NettyClientChannel.this));
                    future.channel().closeFuture().addListener(future1 -> {
                        transformState(State.DISCONNECT, epoch + 1, epoch + 1, null);
                        notifyDisConnectListener(epoch + 1);
                    });
                    notifyConnectListener(epoch + 1);

                } else {
                    String errorMsg = LogUtils.format("can not transform,old state:{},new state:{},old epoch:{},new epoch:{}", tuple3.getV2().name(), State.CONNECTED.name(), epoch, epoch + 1);
                    LOGGER.error(errorMsg);
                    completableFuture.completeExceptionally(new IllegalStateException(errorMsg));
                    NettyClientChannel.this.close();

                }

            } else {

                if (transformState(State.DISCONNECT, epoch, epoch, null)) {
                    if (future.isCancelled()) {
                        completableFuture.completeExceptionally(new CancellationException());
                    } else {
                        completableFuture.completeExceptionally(new ConnectFailException(future.cause()));

                    }
                } else {
                    String errorMsg = LogUtils.format("can not transform,old state:{},new state:{},old epoch:{},new epoch:{}", tuple3.getV2().name(), State.DISCONNECT, epoch, epoch);
                    LOGGER.error(errorMsg);
                    completableFuture.completeExceptionally(new IllegalStateException(errorMsg));
                    NettyClientChannel.this.close();
                }

            }
        });


    }

    @Override
    public CompletableFuture<Tuple2<Integer, Client.Channel>> connect(InetSocketAddress address, int lastEpoch) {
        CompletableFuture<Tuple2<Integer, Client.Channel>> result = new CompletableFuture<>();
        if (transformState(State.CONNECTING, lastEpoch, lastEpoch)) {
            connect0(address, result, lastEpoch);
            return result;
        } else {
            Tuple3<Integer, State, io.netty.channel.Channel> tuple3 = channelAndEpochState.get();
            if (tuple3.getV1() >= lastEpoch + 1 && tuple3.getV2().equals(State.CONNECTED)) {
                result.complete(Tuple2.of(tuple3.getV1(), NettyClientChannel.this));
            } else {
                addConnectListener((channel, epoch) -> result.complete(Tuple2.of(epoch, NettyClientChannel.this)), lastEpoch + 1);
            }
        }
        return result;
    }


    @Override
    public void flush() {
        io.netty.channel.Channel now = channelAndEpochState.get().getV3();
        if (now != null) {
            now.flush();
        }
    }

    public int getEpoch() {
        return channelAndEpochState.get().getV1();
    }

    @Override
    public void close() {
        io.netty.channel.Channel now = channelAndEpochState.get().getV3();
        if (now != null) {
            try {
                now.close().sync();
            } catch (InterruptedException e) {
                LOGGER.error("", e);
            }
        }
        closeEventLoop();
    }

    @Override
    public boolean isConnected() {
        return channelAndEpochState.get().getV2().equals(State.CONNECTED);
    }

    @Override
    public void addDisConnectListener(Listener listener, int epoch) {
        disconnectListeners.addListener(epoch, listener, false);
    }

    @Override
    public void addConnectListener(Listener listener, int epoch) {
        connectListeners.addListener(epoch, listener, true);
    }

    class EpochListeners {
        final Logger LOGGER = LoggerFactory.getLogger(EpochListeners.class);

        BlockingQueue<Listener> listeners;
        volatile int epoch;
        volatile boolean hasNotify = false;
        volatile int newEpoch;

        public EpochListeners(int epoch) {
            this.listeners = new LinkedBlockingQueue<>();
            this.epoch = epoch;
        }

        public void notifyListener() {
            LOGGER.debug(LogUtils.format("notify listeners,epoch:{}", epoch));
            newEpoch = epoch;
            hasNotify = true;
            Listener listener;
            while ((listener = listeners.poll()) != null) {
                LOGGER.debug(LogUtils.format("notify listener,epoch:{},listener:{}", epoch, listener));

                listener.notify(NettyClientChannel.this, epoch);
            }
        }

        public void notifyListener(int epoch) {
            LOGGER.debug(LogUtils.format("notify listeners use new epoch,epoch:{},new epoch:{}", this.epoch, epoch));
            this.newEpoch = epoch;
            hasNotify = true;
            Listener listener;
            while ((listener = listeners.poll()) != null) {
                LOGGER.debug(LogUtils.format("notify listeners use new epoch,epoch:{},new epoch:{},listener:{}", this.epoch, epoch, listener));
                listener.notify(NettyClientChannel.this, epoch);
            }
        }

        public void addListener(Listener listener) {
            LOGGER.debug(LogUtils.format("add listeners ,epoch:{},listener:{}", this.epoch, listener));
            if (hasNotify) {
                LOGGER.debug(LogUtils.format("add listeners when notify,so notify directly ,epoch:{},listener:{}", this.newEpoch, listener));
                listener.notify(NettyClientChannel.this, newEpoch);
                return;
            }
            listeners.add(listener);
            if (hasNotify) {
                Listener peek = listeners.peek();
                if (!listener.equals(peek)) {
                    LOGGER.debug(LogUtils.format("have add listeners when notify,epoch:{},listener:{}", this.newEpoch, listener));
                } else {
                    if (listeners.remove(listener)) {
                        LOGGER.debug(LogUtils.format("have add listeners before notify,so notify directly ,epoch:{},listener:{}", this.newEpoch, listener));
                        listener.notify(NettyClientChannel.this, newEpoch);
                    }
                }
            }
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            EpochListeners that = (EpochListeners) o;

            return epoch == that.epoch;
        }

        @Override
        public int hashCode() {
            return epoch;
        }
    }

    class EpochListenersMap {
        Map<Integer, EpochListeners> map = new ConcurrentHashMap<>();
        //min epoch -> new epoch
        AtomicReference<Tuple2<Integer, Integer>> minAndNewEpoch = new AtomicReference<>(Tuple2.of(0, 0));
        final Logger LOGGER = LoggerFactory.getLogger(EpochListenersMap.class);

        public void addListener(int epoch, Listener listener, boolean useNewEpoch) {
            LOGGER.debug(LogUtils.format("add listener,epoch:{},listener:{}", epoch, listener));
            Tuple2<Integer, Integer> curMinAndNewEpoch = minAndNewEpoch.get();
            int curNewEpoch = curMinAndNewEpoch.getV2();
            if (curNewEpoch >= epoch) {
                LOGGER.debug(LogUtils.format("add old epoch listener,notify directly,cur epoch:{}, add epoch:{},listener:{}"), curNewEpoch, epoch, listener);
                listener.notify(NettyClientChannel.this, epoch);
                return;
            }
            while (true) {
                Tuple2<Integer, Integer> tuple2 = minAndNewEpoch.get();
                int min = tuple2.getV1();
                if (min > epoch) {
                    if (minAndNewEpoch.compareAndSet(tuple2, Tuple2.of(epoch, tuple2.getV2()))) {
                        LOGGER.debug(LogUtils.format("update minAndNewEpoch, ori:{},{}  new:{},{}"), tuple2.getV1(), tuple2.getV2(), epoch, tuple2.getV2());

                        break;
                    }
                } else {
                    break;
                }
            }
            map.computeIfAbsent(epoch, key -> new EpochListeners(epoch)).addListener(listener);
            Integer newEpoch = minAndNewEpoch.get().getV2();
            if (newEpoch > curNewEpoch) {
                LOGGER.debug(LogUtils.format("add listener when notify newEpoch,so notify again, old epoch:{}  new epoch:{}"), curNewEpoch, newEpoch);
                while (true) {
                    Tuple2<Integer, Integer> tuple2 = minAndNewEpoch.get();
                    if (minAndNewEpoch.compareAndSet(tuple2, Tuple2.of(Math.min(curMinAndNewEpoch.getV1(), epoch), tuple2.getV2()))) {
                        LOGGER.debug(LogUtils.format("update minAndNewEpoch, ori:{},{}  new:{},{}"), tuple2.getV1(), tuple2.getV2(), Math.min(curMinAndNewEpoch.getV1(), epoch), tuple2.getV2());
                        break;
                    }
                }
                notifyListener(newEpoch, useNewEpoch);
            }

        }

        /**
         * @param epoch
         * @param useNewEpoch true  {@link com.gmc.net.Client.Channel.Listener#notify(Client.Channel, int)} will use    {@link EpochListenersMap#notifyListener(int, boolean)} epoch rather than use {@link EpochListenersMap#addListener(int, Listener, boolean)} epoch
         */
        public void notifyListener(int epoch, boolean useNewEpoch) {
            LOGGER.debug(LogUtils.format("notify listener,epoch:{}", epoch));
            while (true) {
                Tuple2<Integer, Integer> tuple2 = minAndNewEpoch.get();
                int curEpoch = tuple2.getV2();
                if (curEpoch < epoch) {
                    if (minAndNewEpoch.compareAndSet(tuple2, Tuple2.of(tuple2.getV1(), epoch))) {
                        LOGGER.debug(LogUtils.format("update minAndNewEpoch, ori:{},{}  new:{},{}"), tuple2.getV1(), tuple2.getV2(), tuple2.getV1(), epoch);
                        break;
                    }
                } else {
                    break;
                }
            }
            Tuple2<Integer, Integer> tuple2 = minAndNewEpoch.get();
            int curMinEpoch = tuple2.getV1();

            while (curMinEpoch <= epoch) {
                if (minAndNewEpoch.compareAndSet(tuple2, Tuple2.of(curMinEpoch + 1, tuple2.getV2()))) {
                    LOGGER.debug(LogUtils.format("update minAndNewEpoch, ori:{},{}  new:{},{}"), tuple2.getV1(), tuple2.getV2(), tuple2.getV1(), epoch);
                    EpochListeners epochListeners = map.remove(curMinEpoch);
                    if (epochListeners != null) {
                        if (useNewEpoch) {
                            epochListeners.notifyListener(epoch);
                        } else {
                            epochListeners.notifyListener();
                        }
                    }
                }
                tuple2 = minAndNewEpoch.get();
                curMinEpoch = tuple2.getV1();
            }


        }

    }
}
