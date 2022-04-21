package com.gmc.net;

public interface ClientHeartbeatManager extends LifeCycle {
    Heartbeat createHeartbeat();

    /**
     * reset heartbeat time.if client receive the heartbeat or other response from server,then client should reset heartbeat time
     */
    void resetHeartbeat();

    void sendHeartbeat();


    boolean checkTimeout();
    long getTimeoutInMilliSecond();

    long getSendTimeoutInMilliSecond();

}
