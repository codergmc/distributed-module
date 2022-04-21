package com.gmc.config.convert;


public abstract class AbstractTypeConverter implements TypeConverter {
    protected ConvertSupportResultFactoryInterface convertSupportResultFactory;

    public AbstractTypeConverter(ConvertSupportResultFactoryInterface convertSupportResultFactory) {
        this.convertSupportResultFactory = convertSupportResultFactory;
    }

    public AbstractTypeConverter() {
    }

    public ConvertSupportResultFactoryInterface getConvertSupportResultFactory() {
        return convertSupportResultFactory;
    }

    @Override
    public void configTypeConverter(TypeConverters typeConverters) {
        if (this.convertSupportResultFactory != null) {
            this.convertSupportResultFactory = typeConverters.getConvertSupportResultFactory();
            assert this.convertSupportResultFactory != null;
        }

    }
}
