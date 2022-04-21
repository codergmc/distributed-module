package com.gmc.net;

import java.util.concurrent.Executor;

public class AsyncServerMessageManager implements ServerMessageManager {
    private Executor executor;
    private ServerMessageManager serverMessageManager;

    public AsyncServerMessageManager(Executor executor, ServerMessageManager serverMessageManager) {
        this.executor = executor;
        this.serverMessageManager = serverMessageManager;
    }

    @Override
    public void receiveMessage(MessageWrap messageWrap) {
        executor.execute(() -> serverMessageManager.receiveMessage(messageWrap));
    }

    @Override
    public void channelClose(Server.Channel channel) {
        executor.execute(() -> serverMessageManager.channelClose(channel));

    }

    @Override
    public void channelActive(Server.Channel channel) {
        executor.execute(() -> serverMessageManager.channelActive(channel));

    }
}
