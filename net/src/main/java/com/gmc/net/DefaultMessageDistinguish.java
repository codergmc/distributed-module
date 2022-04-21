package com.gmc.net;

import com.gmc.core.Tuple3;

import java.util.List;
import java.util.function.Supplier;

public class DefaultMessageDistinguish extends AbstractMessageDistinguish {



    public DefaultMessageDistinguish(List<Tuple3<Short, Short, Supplier<Message>>> list) {
        super(list);
    }
}
