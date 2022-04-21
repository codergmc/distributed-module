package com.gmc.net;

import com.gmc.config.ConfigProperty;

public abstract class AbstractServer implements Server {
    protected MessageCodeManager messageCodeManager;
    protected ServerMessageManager serverMessageManager;
    protected ConfigProperty serverConfig;
    protected String host;
    protected int port;

    public AbstractServer(MessageCodeManager messageCodeManager, ServerMessageManager serverMessageManager, ConfigProperty serverConfig) {
        this.messageCodeManager = messageCodeManager;
        this.serverMessageManager = serverMessageManager;
        this.serverConfig = serverConfig;
        host = serverConfig.getConfig(ServerConfig.SERVER_HOST);
        port = serverConfig.getConfig(ServerConfig.SERVER_PORT);

    }
}
