package com.gmc.net;

public class ExceptionUtils {
    public static void check(boolean condition, String msg) {
        if (!condition) {
            throw new IllegalArgumentException(msg);
        }
    }

    public static void checkNotNull(Object value, String msg) {
        if (value == null) {
            throw new NullPointerException(msg);
        }
    }
}
