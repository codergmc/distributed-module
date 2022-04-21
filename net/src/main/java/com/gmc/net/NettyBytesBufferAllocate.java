package com.gmc.net;

import io.netty.buffer.ByteBufAllocator;

public class NettyBytesBufferAllocate implements BytesBufferAllocate {
    private ByteBufAllocator byteBufAllocator;

    public NettyBytesBufferAllocate(ByteBufAllocator byteBufAllocator) {
        this.byteBufAllocator = byteBufAllocator;
    }

    public NettyBytesBufferAllocate() {
        byteBufAllocator = ByteBufAllocator.DEFAULT;
    }

    @Override
    public BytesBuffer allocateFixed(int size, boolean direct) {
        if (direct) {
            return new NettyBytesBuffer(byteBufAllocator.directBuffer(size, size));
        } else {
            return new NettyBytesBuffer(byteBufAllocator.buffer(size, size));
        }
    }


    @Override
    public BytesBuffer allocateAutoIncr(int size, boolean direct) {
        if (direct) {
            return new NettyBytesBuffer(byteBufAllocator.directBuffer(size));
        } else {
            return new NettyBytesBuffer(byteBufAllocator.buffer(size));
        }
    }

}
