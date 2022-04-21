package com.gmc.net;

import com.gmc.config.ConfigProperty;

import java.net.InetSocketAddress;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public abstract class AbstractClient extends AbstractLifeCycle implements Client {
    protected InetSocketAddress remote;
    protected ClientHeartbeatManager clientHeartbeatManager;
    protected ConfigProperty clientConfig;
    protected ClientConnectManager clientConnectManager;
    protected ClientMessageManager clientMessageManager;
    protected MessageCodeManager messageCodeManager;
    protected Channel channel;
    protected CloseableScheduledExecutor executor = new DefaultExecutor(Executors.newSingleThreadScheduledExecutor());

    public AbstractClient(ConfigProperty clientConfig, MessageCodeManager messageCodeManager) {
        this.clientConfig = clientConfig;
        this.messageCodeManager = messageCodeManager;
    }

    @Override
    public CloseableScheduledExecutor getExecutor() {
        return executor;
    }

    class DefaultExecutor implements CloseableScheduledExecutor {
        ScheduledExecutorService executorService;

        public DefaultExecutor(ScheduledExecutorService executorService) {
            this.executorService = executorService;
        }

        @Override
        public void execute(Runnable command) {
            executorService.execute(command);
        }

        @Override
        public void close() throws Exception {
            executorService.shutdown();
        }

        @Override
        public void schedule(Runnable command, long delay, TimeUnit unit) {
            executorService.schedule(command,delay,unit);
        }
    }

    @Override
    public ClientConnectManager getClientConnectManager() {
        return clientConnectManager;
    }

    @Override
    public ClientHeartbeatManager getClientHeartbeatManager() {
        return clientHeartbeatManager;
    }

    @Override
    public ClientMessageManager getClientMessageManager() {
        return clientMessageManager;
    }

    @Override
    public MessageCodeManager getMessageCodeManager() {
        return messageCodeManager;
    }

    @Override
    public ConfigProperty getConfig() {
        return clientConfig;
    }

    protected void close0() {
        try {
            clientHeartbeatManager.close();
        } catch (Exception e) {

        }

    }
}
