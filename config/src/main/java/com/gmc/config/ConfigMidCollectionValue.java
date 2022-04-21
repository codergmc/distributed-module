package com.gmc.config;

import com.gmc.config.convert.TypeConverter;
import com.gmc.config.convert.TypeConverters;

import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * collection or array
 */
public class ConfigMidCollectionValue extends AbstractConfigMidValue {
    private List<ConfigMidValue> configMidValueList = new ArrayList<>();
    private CollectionOperate operate;

    public ConfigMidCollectionValue(TypeWrapper typeWrapper, TypeConverters typeConverters,boolean lazyMerge) {
        super(typeWrapper, typeConverters,lazyMerge);
        assert typeWrapper.isCompositeType();
        assert !typeWrapper.notEnoughComposite();
        assert typeWrapper.getComponentTypeSize() == 1;
        CollectionOperate operate = CollectionOperate.findOperate(typeWrapper.getRawClass());
        assert operate != null;
        this.operate = operate;
    }

    @Override
    public Object generate0() {
        Object result = operate.newInstance();
        for (ConfigMidValue configMidValue : configMidValueList) {
            operate.merge(result, configMidValue.generate());
        }
        return result;

    }

    @Override
    public void merge0(ConfigKey<?> configKey, String key, Object value) {
        TypeWrapper valueType = TypeWrapper.of(value.getClass());
        Collection<?> collection = null;
        if (configKey.getValueType().isRawClassChild(valueType)) {
            collection = ((Collection<?>) value);
        } else {
            TypeConverter.ConvertSupportResult support = typeConverters.support(valueType, configKey.getValueType());
            if (support.isSupport()) {
                collection = support.getTypeConverter().convert(value, valueType, configKey.getValueType());
            }
        }
        if (collection == null) {
            createAndAdd(configKey, key, value);
        } else {
            for (Object o : collection) {
                createAndAdd(configKey, key, o);
            }
        }
    }

    private void createAndAdd(ConfigKey<?> configKey, String key, Object value) {
        ConfigMidValue configMidValue = ConfigMidValue.create(typeWrapper.getComponentType(0), typeConverters,lazyMerge);
        configMidValueList.add(configMidValue);
        configMidValue.merge(configKey, key, value);
    }

}
