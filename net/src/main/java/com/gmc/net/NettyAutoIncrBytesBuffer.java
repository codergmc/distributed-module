package com.gmc.net;

public class NettyAutoIncrBytesBuffer extends AbstractAutoIncrBytesBuffer {


    public NettyAutoIncrBytesBuffer(BytesBufferAllocate bytesBufferAllocate, BytesBuffer bytesBuffer) {
        super(bytesBufferAllocate, bytesBuffer);
    }

    @Override
    protected void ensureEnoughSpace(int capacity) {

    }


}
