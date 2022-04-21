package com.gmc.net;

import com.gmc.config.ConfigProperty;
import com.gmc.core.LogUtils;

import javax.validation.constraints.NotNull;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class ServerBuilder {
    MessageCodeManager messageCodeManager;
    ServerMessageManager serverMessageManager;
    ConfigProperty serverConfig;
    Class<? extends Server> serverClass;

    public static ServerBuilder newBuilder() {
        return new ServerBuilder();
    }

    public ServerBuilder setMessageCodeManager(@NotNull MessageCodeManager messageCodeManager) {
        assert this.messageCodeManager == null;
        assert messageCodeManager != null;
        this.messageCodeManager = messageCodeManager;
        return this;
    }


    public ServerBuilder setServerMessageManager(@NotNull ServerMessageManager serverMessageManager) {
        assert this.serverMessageManager == null;
        assert serverMessageManager != null;
        this.serverMessageManager = serverMessageManager;
        return this;
    }

    public ServerBuilder setConfigProperty(@NotNull ConfigProperty serverConfig) {
        assert serverConfig != null;
        assert this.serverConfig == null;
        this.serverConfig = serverConfig;
        return this;

    }

    public Server build() {
        Constructor<? extends Server> constructor = null;
        try {
            constructor = serverClass.getConstructor(ConfigProperty.class, MessageCodeManager.class, ServerMessageManager.class);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException(LogUtils.format("not find constructor :{}", serverClass.getSimpleName()));
        }
        try {
            return constructor.newInstance(serverConfig, messageCodeManager, serverMessageManager);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new IllegalArgumentException(e);
        }
        return null;
    }
}
