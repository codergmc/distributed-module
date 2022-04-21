package com.gmc.net;


public interface MessageDispatcher {
    void dispatcher(ServerMessageManager.MessageWrap messageWrap);

    /**
     * {@link MessageProcessor} should process message with state
     * {@link com.gmc.net.MessageDispatcher.Handler} should process message without state
     */
    interface Handler<T extends Message> {
        void handleMessage(ServerMessageManager.MessageWrap<T> messageWrap);
    }
}

