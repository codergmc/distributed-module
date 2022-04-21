package com.gmc.net;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class DefaultCommonFuture<T> implements CommonFuture<T> {
    CompletableFuture<T> completableFuture = new CompletableFuture<>();

    @Override
    public void completeValue(T t) {
        completableFuture.complete(t);
    }

    @Override
    public void completeException(Throwable throwable) {
        completableFuture.completeExceptionally(throwable);
    }

    @Override
    public void addListener(Listener<T> listener) {
        completableFuture.thenAccept(v -> listener.notify(v, null));
        completableFuture.exceptionally(throwable -> {
            listener.notify(null, throwable);
            return null;
        });
    }


    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return completableFuture.cancel(mayInterruptIfRunning);
    }

    @Override
    public boolean isCancelled() {
        return completableFuture.isCancelled();
    }

    @Override
    public boolean isDone() {
        return completableFuture.isDone();
    }

    @Override
    public T get() throws InterruptedException, ExecutionException {
        return completableFuture.get();
    }

    @Override
    public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return completableFuture.get(timeout, unit);
    }
}
