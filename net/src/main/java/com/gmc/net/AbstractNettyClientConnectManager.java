package com.gmc.net;

public abstract class AbstractNettyClientConnectManager extends AbstractClientConnectManager implements NettyInit{
    public AbstractNettyClientConnectManager(Client client) {
        super(client);
    }
}
