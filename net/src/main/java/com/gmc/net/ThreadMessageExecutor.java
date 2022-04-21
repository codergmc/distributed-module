package com.gmc.net;

import java.util.concurrent.ExecutorService;

public class ThreadMessageExecutor implements MessageExecutor{
    ExecutorService executorService;

    @Override
    public void execute(Runnable runnable) {
        executorService.submit(runnable);
    }
}
