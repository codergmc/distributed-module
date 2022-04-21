package com.gmc.core;

@FunctionalInterface
public interface ThrowFunction<T, R> {
    R apply(T t) throws Exception;
}
