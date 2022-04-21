package com.gmc.config;

import com.gmc.config.convert.TypeConverters;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class AbstractConfigMidValue implements ConfigMidValue {
    protected TypeWrapper typeWrapper;
    protected TypeConverters typeConverters;
    protected boolean lazyMerge;
    protected List<Consumer<?>> mergeFunction;


    public AbstractConfigMidValue(TypeWrapper typeWrapper, TypeConverters typeConverters, boolean lazyMerge) {
        this.typeWrapper = typeWrapper;
        this.typeConverters = typeConverters;
        this.lazyMerge = lazyMerge;
        if (lazyMerge) {
            mergeFunction = new ArrayList<>();
        }
    }

    public AbstractConfigMidValue setLazyMerge(boolean lazyMerge) {
        this.lazyMerge = lazyMerge;
        if (lazyMerge) {
            mergeFunction = new ArrayList<>();
        }
        return this;
    }

    public TypeWrapper getTypeWrapper() {
        return typeWrapper;
    }

    @Override
    public void merge(ConfigKey<?> configKey, String key, Object value) {
        if (lazyMerge) {
            Object finalValue = value;
            mergeFunction.add((ignore) -> {
                Object v = finalValue;
                if (v instanceof Supplier) {
                    v = ((Supplier<?>) v).get();
                }
                merge0(configKey, key, v);
            });
        } else {
            if (value instanceof Supplier) {
                value = ((Supplier<?>) value).get();
            }
            merge0(configKey, key, value);
        }
    }

    protected abstract void merge0(ConfigKey<?> configKey, String key, Object value);

    @Override
    public Object generate() {
        if (lazyMerge) {
            for (Consumer<?> consumer : mergeFunction) {
                consumer.accept(null);
            }
        }
        return generate0();
    }

    protected abstract Object generate0();


    public TypeConverters getTypeConverters() {
        return typeConverters;
    }
}
