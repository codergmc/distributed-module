package com.gmc.config;

import com.gmc.config.convert.TypeConverters;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ConfigMidValue {
    public static ConfigMidValue create(TypeWrapper typeWrapper, TypeConverters typeConverters,boolean lazyMerge) {
        if (typeWrapper.isRawClassParent(TypeWrapper.of(Collection.class))) {
            return new ConfigMidCollectionValue(typeWrapper, typeConverters,lazyMerge);
        }
        if (typeWrapper.isRawClassParent(TypeWrapper.of(Map.class))) {
            return new ConfigMidMapValue(typeWrapper, typeConverters,lazyMerge);
        }
        try {
            ConfigMidJavaBeanValue configMidJavaBeanValue = new ConfigMidJavaBeanValue(typeWrapper, typeConverters,lazyMerge);
            return configMidJavaBeanValue;
        } catch (Exception e) {
            return new ConfigMidSingleValue(typeWrapper, typeConverters,lazyMerge);

        }


    }

    public Object generate();
    public void merge(ConfigKey<?> configKey, String key, Object value);

}
