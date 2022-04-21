package com.gmc.net;

import java.util.concurrent.Future;

public interface CommonFuture<T> extends Future<T> {
    public static <T> CommonFuture<T> createFuture() {
        return new DefaultCommonFuture<>();
    }

    /**
     * that mean future is success
     *
     * @param t
     */
    void completeValue(T t);


    void completeException(Throwable throwable);

    void addListener(Listener<T> listener);

    interface Listener<T> {
        /**
         * if some error happened when throwable is not null,
         *
         * @param t
         * @param throwable
         */
        void notify(T t, Throwable throwable);
    }
}
