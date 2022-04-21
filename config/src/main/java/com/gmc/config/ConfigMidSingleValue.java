package com.gmc.config;

import com.gmc.config.convert.TypeConverters;

public class ConfigMidSingleValue extends AbstractConfigMidValue {
    private Class<?> aClass;
    private boolean haveMerged = false;
    private Object value;

    public ConfigMidSingleValue(TypeWrapper typeWrapper, TypeConverters typeConverters,boolean lazyMerge) {
        super(typeWrapper,typeConverters,lazyMerge);
        assert typeWrapper.isSimpleType();
        this.aClass = typeWrapper.getRawClass();
    }

    @Override
    public Object generate0() {
        return value;
    }

    @Override
    public void merge0(ConfigKey<?> configKey, String key, Object value) {
        if (haveMerged == true) {
            throw new IllegalArgumentException();
        }
        this.value = typeConverters.convert(value,TypeWrapper.of(value.getClass()),typeWrapper);
        haveMerged = true;

    }
}
