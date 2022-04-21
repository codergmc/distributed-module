package com.gmc.config.convert;


import java.util.function.Supplier;

public abstract class HierarchicalTypeConverter extends AbstractTypeConverter {
    protected Supplier<TypeConverters> typeConverters;

    public HierarchicalTypeConverter(TypeConverters typeConverters, ConvertSupportResultFactoryInterface convertSupportResultFactory) {
        super(convertSupportResultFactory);
        this.typeConverters = () -> typeConverters;
    }

    public HierarchicalTypeConverter(ConvertSupportResultFactoryInterface convertSupportResultFactory) {
        super(convertSupportResultFactory);
    }

    @Override
    public void configTypeConverter(TypeConverters typeConverters) {
        super.configTypeConverter(typeConverters);
        if (this.typeConverters == null) {
            this.typeConverters = () -> typeConverters;
        }
    }
}
