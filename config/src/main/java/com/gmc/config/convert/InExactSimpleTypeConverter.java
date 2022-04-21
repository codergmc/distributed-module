package com.gmc.config.convert;

public abstract class InExactSimpleTypeConverter<FROM,TO> extends SimpleTypeConverter<FROM,TO>{
    public InExactSimpleTypeConverter(TypeConverters typeConverters, ConvertSupportResultFactoryInterface convertSupportResultFactory) {
        super(typeConverters, convertSupportResultFactory);
    }
}
