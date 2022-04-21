package com.gmc.config.convert;

import com.gmc.config.TypeWrapper;

public class ConvertSupportResultFactory implements ConvertSupportResultFactoryInterface {
    @Override
    public TypeConverter.ConvertSupportResult createSimpleType(TypeWrapper child, TypeWrapper parent,TypeConverters typeConverters) {
        return child.getRowClassDistance(parent,typeConverters);
    }




    @Override
    public TypeConverter.ConvertSupportResult createCompositeType(TypeWrapper wrapper, TypeWrapper other,TypeConverters typeConverters) {
        return wrapper.canConvert(other,typeConverters);
    }

    @Override
    public TypeConverter.ConvertSupportResult createComponent(TypeWrapper typeWrapper, TypeWrapper other,TypeConverters typeConverters) {
        return typeWrapper.canComponentConvert(other,typeConverters);
    }

    @Override
    public TypeConverter.ConvertSupportResult createArray(TypeWrapper other) {
        if (other.isArray()) {
            return TypeWrapper.getRowClassArrayDistance();

        }
        return this.createUnSupport();
    }

    @Override
    public TypeConverter.ConvertSupportResult merge(TypeConverter.ConvertSupportResult a, TypeConverter.ConvertSupportResult b) {
        return a.merge(b);
    }

    @Override
    public TypeConverter.ConvertSupportResult mergeChild(TypeConverter.ConvertSupportResult parent, TypeConverter.ConvertSupportResult child) {
        return parent;
    }

    @Override
    public TypeConverter.ConvertSupportResult createUnSupport() {
        return TypeConverter.ConvertSupportResult.ofUnSupport();
    }

    @Override
    public TypeConverter.ConvertSupportResult createHighestPriority() {
        return TypeConverter.ConvertSupportResult.of(0);
    }

}
