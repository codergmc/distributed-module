package com.gmc.net;


public class DefaultServerMessageManager extends AbstractServerMessageManager {

    public DefaultServerMessageManager(MessageDispatcher messageDispatcher, MessageProcessors messageProcessors) {
        super(messageDispatcher, messageProcessors);
    }

    @Override
    public void receiveMessage(MessageWrap messageWrap) {
        messageProcessors.processMessage(messageWrap);
        messageDispatcher.dispatcher(messageWrap);
    }

    @Override
    public void channelClose(Server.Channel channel) {
        messageProcessors.messageChannelClose(channel);

    }

    @Override
    public void channelActive(Server.Channel channel) {
        messageProcessors.channelActive(channel);
    }
}
