package com.gmc.net;

import com.gmc.config.ConfigProperty;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ClosedChannelException;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public class NettyClient extends AbstractClient {
    public NettyClient(ConfigProperty clientConfig, Supplier<Heartbeat> heartbeatSupplier, Function<Message, Long> getMessageIdFunction, BiConsumer<Message, Long> setMessageIdFunction, MessageCodeManager messageCodeManager) {
        super(clientConfig, messageCodeManager);
        this.channel = new NettyClientChannel(this);
        clientHeartbeatManager = new NettyClientHeartbeatManager(this, heartbeatSupplier);
        clientConnectManager = new NettyClientConnectManager(this);
        clientMessageManager = new NettyClientMessageManager(this, getMessageIdFunction, setMessageIdFunction);

    }


    @Override
    public CompletableFuture<Client> connectAsync(InetSocketAddress address) {
        return clientConnectManager.connect(address).handle(new BiFunction<Void, Throwable, Client>() {
            @Override
            public Client apply(Void unused, Throwable throwable) {
                if (throwable != null) {
                    throw new RuntimeException(throwable);
                } else return NettyClient.this;
            }
        });

    }

    @Override
    public Channel getChannel() {
        return channel;
    }

    @Override
    public ClientConnectManager getClientConnectManager() {
        return clientConnectManager;
    }


    @Override
    public void start() {
        channel.init();
        clientConnectManager.start();
        clientHeartbeatManager.start();
        clientMessageManager.start();
    }

    @Override
    public void close0() {
        super.close0();
        try {
            clientConnectManager.close();
        } catch (IOException e) {
            LOGGER.error("", e);
        }
        try {
            clientMessageManager.close();
        } catch (IOException e) {
            LOGGER.error("", e);
        }
        channel.close();
        try {
            executor.close();
        } catch (Exception e) {
            LOGGER.error("", e);
        }
    }


    @Override
    public void send(Writeable writeable, SendNotify sendNotify, ReceiveNotify receiveNotify) {
        Message message = (Message) writeable;
        clientMessageManager.putMessage(message, sendNotify, receiveNotify);
    }


}
