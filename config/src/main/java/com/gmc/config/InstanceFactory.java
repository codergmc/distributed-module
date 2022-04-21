package com.gmc.config;

import java.util.AbstractList;
import java.util.AbstractSequentialList;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public class InstanceFactory {
    private static Map<Class<?>, Supplier<?>> INSTANCE_MAP = new ConcurrentHashMap<>();

    static {
        INSTANCE_MAP.put(Set.class, LinkedHashSet::new);
        INSTANCE_MAP.put(AbstractSet.class, LinkedHashSet::new);
        INSTANCE_MAP.put(NavigableSet.class, TreeSet::new);
        INSTANCE_MAP.put(SortedSet.class, TreeSet::new);
        INSTANCE_MAP.put(TreeSet.class, TreeSet::new);
        INSTANCE_MAP.put(HashSet.class, HashSet::new);
        INSTANCE_MAP.put(LinkedHashSet.class, LinkedHashSet::new);


        INSTANCE_MAP.put(List.class, LinkedList::new);
        INSTANCE_MAP.put(AbstractList.class, LinkedList::new);
        INSTANCE_MAP.put(AbstractSequentialList.class, LinkedList::new);
        INSTANCE_MAP.put(ArrayList.class, ArrayList::new);
    }

    public static <T> T create(Class<?> aClass) {
        return (T) INSTANCE_MAP.get(aClass).get();
    }

    public static void registor(Class<?> aClass, Supplier<?> supplier) {
        INSTANCE_MAP.put(aClass, supplier);
    }
}
