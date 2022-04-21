package com.gmc.config;

import com.gmc.config.convert.TypeConverter;
import com.gmc.config.convert.TypeConverters;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class ConfigMidMapValue extends AbstractConfigMidValue {
    private Map<ConfigMidValue, ConfigMidValue> midValueMap = new HashMap<>();
    private TypeWrapper keyType;
    private TypeWrapper valueType;
    private CollectionOperate operate;

    public ConfigMidMapValue(TypeWrapper typeWrapper, TypeConverters typeConverters,boolean lazyMerge) {
        super(typeWrapper, typeConverters,lazyMerge);
        assert typeWrapper.isRawClassParent(TypeWrapper.of(Map.class));
        assert typeWrapper.getComponentTypeSize() == 2;
        this.operate = CollectionOperate.findOperate(typeWrapper.getRawClass());
        this.keyType = typeWrapper.getComponentType(0);
        this.valueType = typeWrapper.getComponentType(1);
    }

    @Override
    public Object generate0() {
        Object result = operate.newInstance();
        midValueMap.forEach((k, v) -> {
            operate.merge(result, k.generate(), v.generate());
        });
        return result;

    }

    @Override
    public void merge0(ConfigKey<?> configKey, String key, Object value) {
        TypeWrapper valueType = TypeWrapper.of(value.getClass());
        Map<?, ?> map = null;
        if (valueType.isRawClassParent(configKey.getValueType())) {
            map = (Map<?, ?>) value;
        } else {
            TypeConverter.ConvertSupportResult support = typeConverters.support(valueType, configKey.getValueType());
            if (support.isSupport()) {
                map = support.getTypeConverter().convert(value, valueType, configKey.getValueType());
            }
        }
        if (map == null) {
            createAndAdd(configKey, key, value);
        } else {
            map.forEach((k, v) -> {
                createAndAdd(configKey, k.toString(), v);
            });
        }
    }

    private void createAndAdd(ConfigKey<?> configKey, String key, Object value) {
        ConfigMidValue keyMidValue = ConfigMidValue.create(keyType, typeConverters,lazyMerge);
        ConfigMidValue valueMidValue = ConfigMidValue.create(valueType, typeConverters,lazyMerge);
        keyMidValue.merge(configKey, key, key);
        valueMidValue.merge(configKey, key, value);
        midValueMap.put(keyMidValue, valueMidValue);
    }
}
