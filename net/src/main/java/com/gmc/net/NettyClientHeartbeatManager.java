package com.gmc.net;

import io.netty.channel.Channel;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class NettyClientHeartbeatManager extends AbstractNettyClientHeartbeatManager {

    public NettyClientHeartbeatManager(NettyClient client, Supplier<Heartbeat> supplier) {
        super(client, supplier);
    }



    @Override
    public void nettyInit(Channel channel) {
        channel.pipeline().addLast(NettyUtils.getNettyClientResetHeartbeatHandler(this));
        channel.pipeline().addLast(NettyUtils.getNettyClientHeartbeat(this, this.getSendTimeoutInMilliSecond(), TimeUnit.MILLISECONDS));
    }

    @Override
    protected void close0() {

    }
}
