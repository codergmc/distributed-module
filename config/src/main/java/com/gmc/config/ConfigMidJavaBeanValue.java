package com.gmc.config;

import com.gmc.config.convert.TypeConverters;
import com.gmc.core.LogUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class ConfigMidJavaBeanValue extends AbstractConfigMidValue {
    static final Logger LOGGER = LoggerFactory.getLogger(ConfigMidJavaBeanValue.class);
    private Map<String, Method> setMethodMap = new HashMap<>();
    private Map<String, ConfigMidValue> fieldMidValue = new HashMap<>();

    public ConfigMidJavaBeanValue(TypeWrapper typeWrapper, TypeConverters typeConverters,boolean lazyMerge) throws Exception {
        super(typeWrapper, typeConverters,lazyMerge);
        BeanUtils.processJavaBean(typeWrapper, (field, getMethod, setMethod) -> {
            setMethodMap.put(field.getName(), setMethod);
            fieldMidValue.put(field.getName(), ConfigMidValue.create(TypeWrapper.of(field.getGenericType()), typeConverters,lazyMerge));
        });
    }


    @Override
    public Object generate0() {
        try {
            Object result = typeWrapper.getRawClass().getConstructor().newInstance();
            for (Map.Entry<String, ConfigMidValue> entry : fieldMidValue.entrySet()) {
                String fieldName = entry.getKey();
                ConfigMidValue midValue = entry.getValue();
                Method setMethod = setMethodMap.get(fieldName);
                setMethod.setAccessible(true);
                setMethod.invoke(result, midValue.generate());
            }
            return result;

        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public void merge0(ConfigKey<?> configKey, String key, Object value) {
        String key1 = configKey.getKey();
        String fieldName = key1.substring(key1.length() + 1);
        int index = fieldName.indexOf(".");
        if (index > 0) {
            fieldName = fieldName.substring(0, index);
        }
        ConfigMidValue configMidValue = fieldMidValue.get(fieldName);
        if (configMidValue == null) {
            LOGGER.warn(LogUtils.format("un support config key :{}", key));
            return;
        }
        configMidValue.merge(configKey, key, value);


    }
}
