package com.gmc.net;


public class HeartbeatMessage extends Message implements Heartbeat{
    static final short TYPE=0;
    static final short VERSION=0;
    public HeartbeatMessage() {
        super(new DefaultMessageHead(TYPE, VERSION), EmptyMessageBody.INSTANCE);
    }
}
