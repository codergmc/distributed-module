package com.gmc.core;

public class ExceptionUtils {
    public static void check(boolean condition, String msg) {
        if (!condition) {
            throw new IllegalArgumentException(msg);
        }
    }
}
