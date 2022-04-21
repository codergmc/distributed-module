package com.gmc.net;

import com.gmc.core.Tuple2;
import com.gmc.core.Tuple3;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public abstract class AbstractMessageDistinguish implements MessageDistinguish {
    protected Map<Tuple2<Short, Short>, Supplier<Message>> messageMap = new HashMap<>();

    public AbstractMessageDistinguish(@NotNull List<Tuple3<Short, Short, Supplier<Message>>> list) {
        assert list != null;
        for (Tuple3<Short, Short, Supplier<Message>> tuple3 : list) {
            messageMap.put(Tuple2.of(tuple3.getV1(), tuple3.getV2()), tuple3.getV3());
        }
    }

    @Override
    public Message distinguish(BytesBuffer buffer) {
        return buffer.readAndResetReadIndex(ignore -> {
            short type = buffer.readShort();
            short version = buffer.readShort();
            Supplier<Message> messageSupplier = messageMap.get(Tuple2.of(type, version));
            if (messageSupplier == null) {
                throw new IllegalArgumentException();
            }
            return messageSupplier.get();
        });
    }

    public void register(short type, short version, Supplier<Message> supplier) {
        messageMap.put(Tuple2.of(type, version), supplier);
    }
}
