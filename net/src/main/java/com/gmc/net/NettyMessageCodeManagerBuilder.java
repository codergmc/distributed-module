package com.gmc.net;

import com.gmc.core.Tuple3;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class NettyMessageCodeManagerBuilder {
    private CodeMode all = new AllSizeCodeMode(4);
    private CodeMode head = new SizeCodeMode(4);
    private CodeMode body = new SizeCodeMode(4);
    private MessageDistinguish messageDistinguish = new DefaultMessageDistinguish(Collections.emptyList());
    private List<Tuple3<Short, Short, Supplier<Message>>> list = new ArrayList<>();
    private BytesBufferAllocate bytesBufferAllocate = new NettyBytesBufferAllocate();
    private boolean releaseByteBufferForDecode = true;
    public NettyMessageCodeManagerBuilder setAll(CodeMode all) {
        this.all = all;
        return this;
    }

    public NettyMessageCodeManagerBuilder setHead(CodeMode head) {
        this.head = head;
        return this;
    }

    public NettyMessageCodeManagerBuilder setBody(CodeMode body) {
        this.body = body;
        return this;
    }

    public NettyMessageCodeManagerBuilder addMessageDistinguish(int type, int version, Supplier<Message> supplier) {
        list.add(Tuple3.of((short) type, (short) version, supplier));
        return this;
    }

    public NettyMessageCodeManagerBuilder setMessageDistinguish(MessageDistinguish messageDistinguish) {
        this.messageDistinguish = messageDistinguish;
        return this;
    }


    public NettyMessageCodeManagerBuilder setBytesBufferAllocate(BytesBufferAllocate bytesBufferAllocate) {
        this.bytesBufferAllocate = bytesBufferAllocate;
        return this;
    }

    public NettyMessageCodeManagerBuilder setReleaseByteBufferForDecode(boolean releaseByteBufferForDecode) {
        this.releaseByteBufferForDecode = releaseByteBufferForDecode;
        return this;
    }

    public static NettyMessageCodeManagerBuilder builder() {
        return new NettyMessageCodeManagerBuilder();
    }

    public MessageCodeManager build() {
        if (!list.isEmpty()) {
            list.forEach(tuple3 -> ((AbstractMessageDistinguish) messageDistinguish).register(tuple3.getV1(), tuple3.getV2(), tuple3.getV3()));
        }
        return new NettyMessageCodeManager(bytesBufferAllocate, messageDistinguish, all, head, body,releaseByteBufferForDecode);
    }


}
