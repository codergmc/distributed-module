package com.gmc.config.convert;

import com.gmc.config.TypeWrapper;

public class ObjectToStringConverter extends InExactSimpleTypeConverter<Object,String> {

    public ObjectToStringConverter(TypeConverters typeConverters, ConvertSupportResultFactoryInterface convertSupportResultFactory) {
        super(typeConverters, convertSupportResultFactory);
    }

    @Override
    public <T> T convert0(Object value, TypeWrapper fromType, TypeWrapper toType) {
        return (T) value.toString();
    }
}
