package com.gmc.net;

import com.gmc.core.Tuple2;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicReferenceArray;

public class AtomicEpochHistoryReference<T> {
    AtomicInteger epoch = new AtomicInteger(0);
    AtomicReferenceArray<T> value = new AtomicReferenceArray<>(100);

    public int set(T t) {
        int index = epoch.getAndIncrement();
        value.set(index, t);
        return index;

    }

    public T get(int index) {
        return value.get(index);
    }
}
