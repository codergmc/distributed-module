package com.gmc.net;

import io.netty.channel.Channel;

import java.util.function.Supplier;

public abstract class AbstractNettyClientHeartbeatManager extends AbstractClientHeartbeatManager implements NettyInit{


    public AbstractNettyClientHeartbeatManager(Client client, Supplier<Heartbeat> supplier) {
        super(client, supplier);
    }
}
