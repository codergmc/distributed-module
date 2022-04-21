package com.gmc.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

public class AtomicUtils {
    static final Logger LOGGER = LoggerFactory.getLogger(AtomicUtils.class);

    public static <T> boolean set(AtomicReference<T> reference, ThrowFunction<T, Boolean> testFunction, ThrowFunction<T, T> valueFunction) {

        Function<T, Boolean> tf = LambdaUtils.throwFunction(testFunction);
        Function<T, T> vf = LambdaUtils.throwFunction(valueFunction);
        T t = reference.get();
        while (true) {
            try {
                if (tf.apply(t)) {
                    if (reference.compareAndSet(t, vf.apply(t))) {
                        return true;
                    } else {
                        t = reference.get();
                    }
                } else {
                    return false;
                }
            } catch (Exception e) {
                return false;
            }


        }

    }

    public static boolean set(AtomicInteger reference, ThrowFunction<Integer, Boolean> testFunction, ThrowFunction<Integer, Integer> valueFunction) {

        Function<Integer, Boolean> tf = LambdaUtils.throwFunction(testFunction);
        Function<Integer, Integer> vf = LambdaUtils.throwFunction(valueFunction);
        Integer t = reference.get();
        while (true) {
            try {
                if (tf.apply(t)) {
                    if (reference.compareAndSet(t, vf.apply(t))) {
                        return true;
                    } else {
                        t = reference.get();
                    }
                } else {
                    return false;
                }
            } catch (Exception e) {
                return false;
            }


        }

    }

}
