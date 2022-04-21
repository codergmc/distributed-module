package com.gmc.config;

import com.gmc.config.convert.TypeConverter;
import com.gmc.config.convert.TypeConverters;
import com.sun.jdi.VoidValue;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.List;

public class ConfigMidArrayValue extends AbstractConfigMidValue {
    private List<ConfigMidValue> configMidValues;

    public ConfigMidArrayValue(TypeWrapper typeWrapper, TypeConverters typeConverters,boolean lazyMerge) {
        super(typeWrapper, typeConverters,lazyMerge);
        assert typeWrapper.isArray();
    }

    @Override
    protected Object generate0() {
        Object array = Array.newInstance(typeWrapper.getComponentType(0).getRawClass(), configMidValues.size());
        for (int i = 0; i < configMidValues.size(); i++) {
            Array.set(array, i, configMidValues.get(i).generate());
        }
        return array;
    }

    @Override
    protected void merge0(ConfigKey<?> configKey, String key, Object value) {
        TypeWrapper valueType = TypeWrapper.of(value.getClass());
        Object array = null;
        if (valueType.isArray()) {
            array = value;

        } else {
            TypeConverter.ConvertSupportResult support = typeConverters.support(valueType, configKey.getValueType());
            if (support.isSupport()) {
                array = support.getTypeConverter().convert(value, valueType, configKey.getValueType());

            }
        }
        if (array == null) {
            createAndAdd(configKey, key, value);

        } else {
            int length = Array.getLength(value);
            if (length != 0) {
                for (int index = 0; index < length; index++) {
                    Object o = Array.get(value, index);
                    createAndAdd(configKey, key, o);
                }
            }
        }
    }


    private void createAndAdd(ConfigKey<?> configKey, String key, Object value) {
        ConfigMidValue configMidValue = ConfigMidValue.create(typeWrapper.getComponentType(0), typeConverters,lazyMerge);
        configMidValue.merge(configKey, key, value);
        configMidValues.add(configMidValue);
    }
}
