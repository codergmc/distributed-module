package com.gmc.config.convert;

import com.gmc.config.TypeWrapper;

public interface ConvertSupportResultFactoryInterface {
    TypeConverter.ConvertSupportResult createSimpleType(TypeWrapper child , TypeWrapper parent,TypeConverters typeConverters);
    TypeConverter.ConvertSupportResult createCompositeType(TypeWrapper wrapper , TypeWrapper other,TypeConverters typeConverters);

    TypeConverter.ConvertSupportResult createComponent(TypeWrapper typeWrapper ,TypeWrapper other,TypeConverters typeConverters);

    TypeConverter.ConvertSupportResult createArray(TypeWrapper other);

    TypeConverter.ConvertSupportResult merge(TypeConverter.ConvertSupportResult a, TypeConverter.ConvertSupportResult b);

    TypeConverter.ConvertSupportResult mergeChild(TypeConverter.ConvertSupportResult parent, TypeConverter.ConvertSupportResult child);
    TypeConverter.ConvertSupportResult createUnSupport();
    TypeConverter.ConvertSupportResult createHighestPriority();

}
