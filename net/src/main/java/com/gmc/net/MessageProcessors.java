package com.gmc.net;

import java.util.List;

public class MessageProcessors implements MessageProcessor {
    List<MessageProcessor> processors;

    public MessageProcessors(List<MessageProcessor> processors) {
        this.processors = processors;
    }

    @Override
    public void processMessage(ServerMessageManager.MessageWrap messageWrap) {
        processors.forEach(processor -> processor.processMessage(messageWrap));
    }

    @Override
    public void messageChannelClose(Server.Channel channel) {
        processors.forEach(processor -> processor.messageChannelClose(channel));

    }

    @Override
    public void channelActive(Server.Channel channel) {
        processors.forEach(processor -> processor.channelActive(channel));
    }
}
