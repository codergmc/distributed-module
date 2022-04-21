package com.gmc.net;

import java.util.concurrent.TimeUnit;

public class Timeout {
    private final TimeUnit timeUnit;
    private final int timeout;
    private volatile long beginTime = -1;

    public Timeout(TimeUnit timeUnit, int timeout) {
        this.timeUnit = timeUnit;
        this.timeout = timeout;
    }

    public void begin() {
        if (beginTime < 0) {
            beginTime = System.currentTimeMillis();
        }
    }

    public long getTimeoutInMilli() {
        return timeUnit.toMillis(timeout);
    }

    public void forceBegin() {
        beginTime = System.currentTimeMillis();
    }

    public boolean timeout() {
        if (beginTime < 0) {
            return false;
        }
        long cur = System.currentTimeMillis();
        return beginTime + timeUnit.toMillis(timeout) < cur;
    }

    public long remainTimeInMillis() {
        if (beginTime < 0) {
            return timeUnit.toMillis(timeout);
        }
        return timeUnit.toMillis(timeout) + beginTime - System.currentTimeMillis();
    }

    public void reset() {
        beginTime = -1;
    }
}
