package com.gmc.core;

import java.util.function.Function;

public class LambdaUtils {
    public static <T, R> Function<T, R> throwFunction(ThrowFunction<T, R> function) {
        return p -> {
            try {
                return function.apply(p);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }
}
