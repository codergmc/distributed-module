package com.gmc.net;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public abstract class AbstractClientHeartbeatManager extends AbstractLifeCycle implements ClientHeartbeatManager {
    static final Logger LOGGER = LoggerFactory.getLogger(AbstractClientHeartbeatManager.class);
    protected Client client;
    protected Timeout timeout;
    protected Supplier<Heartbeat> supplier;
    protected Client.Channel channel;
    protected Timeout sendTimeout;

    public AbstractClientHeartbeatManager(Client client, Supplier<Heartbeat> supplier) {
        this.client = client;
        this.channel = client.getChannel();
        Integer heartbeatTimeout = client.getConfig().getConfig(ClientConfig.HEARTBEAT_TIME_OUT);
        Integer heartbeatSendTimeout = client.getConfig().getConfig(ClientConfig.HEARTBEAT_TIME_SEND_OUT);
        this.sendTimeout = new Timeout(TimeUnit.MILLISECONDS, heartbeatSendTimeout);

        this.timeout = new Timeout(TimeUnit.MILLISECONDS, heartbeatTimeout);

        this.supplier = supplier;
    }

    @Override
    public void start() {
        client.getExecutor().schedule(new Runnable() {
            @Override
            public void run() {
                if (!checkTimeout()) {
                    client.getExecutor().schedule(this, timeout.remainTimeInMillis(), TimeUnit.MILLISECONDS);
                }
            }
        }, timeout.getTimeoutInMilli(), TimeUnit.MILLISECONDS);
    }

    @Override
    public Heartbeat createHeartbeat() {
        return supplier.get();
    }

    @Override
    public void sendHeartbeat() {
        client.send(createHeartbeat(), Client.SendNotify.DO_NOTHING, new Client.ReceiveNotify() {
            @Override
            public void receive(Readable readable) {
                resetHeartbeat();
            }

            @Override
            public void onException(Throwable throwable) {
                LOGGER.error("", throwable);
                sendHeartbeat();

            }
        });
    }

    @Override
    public void resetHeartbeat() {
        timeout.forceBegin();
    }

    @Override
    public long getTimeoutInMilliSecond() {
        return timeout.getTimeoutInMilli();
    }

    @Override
    public long getSendTimeoutInMilliSecond() {
        return sendTimeout.getTimeoutInMilli();
    }

    @Override
    public boolean checkTimeout() {
        if (timeout.timeout()) {
            try {
                client.close();
            } catch (Exception e) {
                LOGGER.error("", e);
            }
            return true;
        }
        return false;
    }
}
