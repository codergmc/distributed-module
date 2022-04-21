package com.gmc.net;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;

public class NettyClientConnectManager extends AbstractNettyClientConnectManager{

    public NettyClientConnectManager(NettyClient client) {
        super(client);
    }


    @Override
    public void nettyInit(Channel channel) {

    }
}
