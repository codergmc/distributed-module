package com.gmc.net;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class TestUtils {


    public static boolean waitForTrue(long timeout, TimeUnit timeUnit, Supplier<Boolean> supplier) {
        long endTime = System.currentTimeMillis() + timeUnit.toMillis(timeout);
        while (true) {
            Boolean aBoolean = supplier.get();
            if (aBoolean != null && aBoolean) {
                return true;
            }
            if (System.currentTimeMillis() > endTime) {
                return false;
            }
            try {
                TimeUnit.MILLISECONDS.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean waitForTrue(Supplier<Boolean> supplier) {
        return waitForTrue(Integer.MAX_VALUE, TimeUnit.SECONDS, supplier);
    }
}
