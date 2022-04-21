package com.gmc.net;

import java.util.function.BiConsumer;
import java.util.function.Function;

public abstract class AbstractNettyClientMessageManager extends AbstractClientMessageManager implements NettyInit{
    public AbstractNettyClientMessageManager(Client client, Function<Message, Long> getMessageIdFunction, BiConsumer<Message, Long> setMessageIdFunction) {
        super(client, getMessageIdFunction, setMessageIdFunction);
    }
}
