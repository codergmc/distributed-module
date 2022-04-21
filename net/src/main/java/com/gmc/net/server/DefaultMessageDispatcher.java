package com.gmc.net.server;

import com.gmc.config.TypeWrapper;
import com.gmc.core.ReflectUtils;
import com.gmc.net.Message;
import com.gmc.net.MessageDispatcher;
import com.gmc.net.ServerMessageManager;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class DefaultMessageDispatcher extends AbstractMessageDispatcher implements MessageDispatcher {

    public DefaultMessageDispatcher(List<Handler<? extends Message>> handlers) {
        super(handlers);
    }

    @Override
    public void dispatcher(ServerMessageManager.MessageWrap messageWrap) {
        Handler<? extends Message> handler = handlerMap.get(messageWrap.getMessage().getClass());
        assert handler != null;
        handler.handleMessage(messageWrap);
    }
}
