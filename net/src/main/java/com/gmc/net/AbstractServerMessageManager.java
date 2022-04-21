package com.gmc.net;

public abstract class AbstractServerMessageManager implements ServerMessageManager {
    protected MessageDispatcher messageDispatcher;
    protected MessageProcessors messageProcessors;

    public AbstractServerMessageManager(MessageDispatcher messageDispatcher, MessageProcessors messageProcessors) {
        this.messageDispatcher = messageDispatcher;
        this.messageProcessors = messageProcessors;
    }

}
