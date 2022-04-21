package com.gmc.net;

public class DefaultMessageExecutor implements MessageExecutor {
    @Override
    public void execute(Runnable runnable) {
        runnable.run();
    }
}
