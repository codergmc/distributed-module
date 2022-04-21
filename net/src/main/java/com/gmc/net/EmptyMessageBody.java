package com.gmc.net;

public class EmptyMessageBody implements MessageBody {
    public static final MessageBody INSTANCE = new EmptyMessageBody();

    @Override
    public void read(BytesBuffer buffer) {

    }

    @Override
    public void write(BytesBuffer buffer) {

    }
}
