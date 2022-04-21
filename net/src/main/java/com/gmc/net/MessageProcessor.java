package com.gmc.net;

/**
 *  {@link MessageProcessor} should process message with state
 *  {@link com.gmc.net.MessageDispatcher.Handler} should process message without state
 */
public interface MessageProcessor {
    void processMessage(ServerMessageManager.MessageWrap messageWrap);
    void messageChannelClose(Server.Channel channel);
    void channelActive(Server.Channel channel);
}
