package com.gmc.net;

import io.netty.channel.Channel;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class NettyClientMessageManager extends AbstractNettyClientMessageManager {
    public NettyClientMessageManager(Client client, Function<Message, Long> getMessageIdFunction, BiConsumer<Message, Long> setMessageIdFunction) {
        super(client, getMessageIdFunction, setMessageIdFunction);
    }

    @Override
    public void nettyInit(Channel channel) {
        channel.pipeline().addLast(NettyUtils.getNettyClientMessageHandler(this));

    }


}
