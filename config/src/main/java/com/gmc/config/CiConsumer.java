package com.gmc.config;
@FunctionalInterface
public interface CiConsumer<A,B,C> {
    void accept(A a, B b,C c);

}
