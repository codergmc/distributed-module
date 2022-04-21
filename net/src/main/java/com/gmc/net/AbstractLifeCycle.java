package com.gmc.net;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;

public abstract class AbstractLifeCycle implements LifeCycle {
    static final Logger LOGGER = LoggerFactory.getLogger(AbstractLifeCycle.class);
    protected volatile boolean hasClosed = false;
    protected final Object closeLock = new Object();

    public abstract void start();

    @Override
    public void close() {
        if (!hasClosed) {
            synchronized (closeLock) {
                hasClosed = true;
                if (!hasClosed) {
                    try {
                        close0();
                    } catch (Exception e) {
                        LOGGER.error("", e);
                    }
                }
            }

        }
    }

    protected abstract void close0();
}
