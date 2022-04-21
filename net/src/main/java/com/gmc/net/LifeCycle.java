package com.gmc.net;

import java.io.Closeable;

public interface LifeCycle extends Closeable {
    void start();
}
