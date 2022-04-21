package com.gmc.net.server;

import com.gmc.core.ReflectUtils;
import com.gmc.net.Message;
import com.gmc.net.MessageDispatcher;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class AbstractMessageDispatcher implements MessageDispatcher {
    protected Map<Class<? extends Message>, Handler<? extends Message>> handlerMap = new ConcurrentHashMap<>();
    protected List<Handler<? extends Message>> handlers = new CopyOnWriteArrayList<>();
    public AbstractMessageDispatcher(List<Handler<? extends Message>> handlers) {
        this.handlers.addAll(handlers);
        for (Handler<? extends Message> handler : handlers) {
            Class messageType = ReflectUtils.getRowClass(ReflectUtils.getComponentType(handler.getClass(), 0));
            handlerMap.put(messageType, handler);
        }

    }
}
