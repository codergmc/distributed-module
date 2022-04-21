package com.gmc.config.convert;

import com.gmc.core.LogUtils;
import com.gmc.config.TypeWrapper;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public abstract class SimpleTypeConverter<FROM, TO> extends HierarchicalTypeConverter {
    protected TypeWrapper fromType;
    protected TypeWrapper toType;

    public SimpleTypeConverter(TypeConverters typeConverters, ConvertSupportResultFactoryInterface convertSupportResultFactory) {
        super(typeConverters, convertSupportResultFactory);
        Type genericSuperclass = this.getClass().getGenericSuperclass();
        if (SimpleTypeConverter.class.isAssignableFrom(this.getClass())) {
            while (true) {
                if (genericSuperclass instanceof ParameterizedType) {
                    Type rawType = ((ParameterizedType) genericSuperclass).getRawType();
                    if (rawType instanceof Class) {
                        if (((Class<?>) rawType).isAssignableFrom(ExactSimpleTypeConverter.class) || ((Class<?>) rawType).isAssignableFrom(InExactSimpleTypeConverter.class)) {
                            Type[] actualTypeArguments = ((ParameterizedType) genericSuperclass).getActualTypeArguments();
                            fromType = TypeWrapper.of((Class<FROM>) actualTypeArguments[0]);
                            toType = TypeWrapper.of((Class<FROM>) actualTypeArguments[1]);
                            if (ExactSimpleTypeConverter.class.isAssignableFrom(this.getClass())) {
                                typeConverters.register(fromType, toType, this);
                            }
                            break;
                        } else {
                            genericSuperclass = ((Class<?>) rawType).getGenericSuperclass();
                            continue;
                        }
                    }
                }
                throw new IllegalArgumentException();
            }
        }
    }

    @Override
    public <T> T convert(Object value, TypeWrapper fromType, TypeWrapper toType) {
        if (!fromType.isRawClassChild(TypeWrapper.of(value.getClass()))) {
            throw new IllegalArgumentException(LogUtils.format("value and value type not match,type:{} value:{} ", fromType, value));
        }
        if (fromType.equals(toType)) {
            return (T) value;
        } else {
            return convert0(value, fromType, toType);
        }
    }

    public abstract <T> T convert0(Object value, TypeWrapper fromType, TypeWrapper toType);

    public ConvertSupportResult support(TypeWrapper fromType, TypeWrapper toType) {
        return typeConverters.get().getConvertSupportResultFactory().merge(typeConverters.get().getConvertSupportResultFactory().createSimpleType(fromType, this.fromType, typeConverters.get()), typeConverters.get().getConvertSupportResultFactory().createSimpleType(toType, this.toType, typeConverters.get())).setTypeConverter(this);
    }

    @Override
    public ConvertSupportResult support(Object value, TypeWrapper fromType, TypeWrapper toType) {
        if (fromType.isRowClassInstance(value)) {
            support(fromType, toType);
        }
        throw new IllegalArgumentException();
    }
}
