package com.gmc.net;

import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public interface CloseableScheduledExecutor extends Executor, AutoCloseable {


    void schedule(Runnable command, long delay, TimeUnit unit);

}
