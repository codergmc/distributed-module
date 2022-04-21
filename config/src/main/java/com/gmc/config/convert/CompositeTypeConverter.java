package com.gmc.config.convert;

import com.gmc.config.TypeWrapper;

public abstract class CompositeTypeConverter extends HierarchicalTypeConverter {


    public CompositeTypeConverter(TypeConverters typeConverters, ConvertSupportResultFactoryInterface convertSupportResultFactory) {
        super(typeConverters, convertSupportResultFactory);
    }


    protected abstract TypeWrapper getType(Object value, TypeWrapper typeWrapper);

    protected abstract ConvertSupportResult checkRowClass(TypeWrapper fromType, TypeWrapper toType);

    protected abstract ConvertSupportResult checkComponent(TypeWrapper fromType, TypeWrapper toType);

    @Override
    public ConvertSupportResult support(TypeWrapper fromType, TypeWrapper toType) {
        ConvertSupportResult parent = check(fromType, toType);
        if (parent.isSupport()) {
            ConvertSupportResult child = checkComponent(fromType, toType);
            parent = convertSupportResultFactory.mergeChild(parent,child);
        }
        return parent;
    }

    protected ConvertSupportResult check(TypeWrapper fromType, TypeWrapper toType) {
        if (fromType.isCompositeType() && toType.isCompositeType()) {
            return checkRowClass(fromType, toType);
        }
        return ConvertSupportResult.ofUnSupport();
    }

    @Override
    public ConvertSupportResult support(Object value, TypeWrapper fromType, TypeWrapper toType) {
        ConvertSupportResult result = check(fromType, toType);
        if (result.isSupport()) {
            if (fromType.notEnoughComposite()) {
                fromType = getType(value, fromType);
            }
            return support(fromType, toType);
        }

        return ConvertSupportResult.ofUnSupport();


    }


}