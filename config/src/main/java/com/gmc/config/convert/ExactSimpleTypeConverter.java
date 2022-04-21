package com.gmc.config.convert;

import com.gmc.config.TypeWrapper;

public abstract class ExactSimpleTypeConverter<FROM,TO> extends SimpleTypeConverter<FROM,TO>{
    public ExactSimpleTypeConverter(TypeConverters typeConverters, ConvertSupportResultFactoryInterface convertSupportResultFactory) {
        super(typeConverters, convertSupportResultFactory);
    }

    @Override
    public ConvertSupportResult support(TypeWrapper fromType, TypeWrapper toType) {
        return super.support(fromType, toType);
    }
}
