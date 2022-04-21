package com.gmc.config.convert;

import com.gmc.core.Tuple2;
import com.gmc.config.TypeWrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class TypeConverters {
    protected Map<Tuple2<TypeWrapper, TypeWrapper>, TypeConverter> typeConverterMap = new ConcurrentHashMap<>();
    protected List<TypeConverter> typeConverterList = new ArrayList<>();
    protected Map<TypeWrapper, Set<TypeWrapper>> simpleTypeWrapperMap = new ConcurrentHashMap<>();
    protected ConvertSupportResultFactoryInterface convertSupportResultFactory;

    public TypeConverters(List<TypeConverter> typeConverterList, ConvertSupportResultFactoryInterface convertSupportResultFactory) {
        assert typeConverterList != null;
        assert convertSupportResultFactory != null;
        this.convertSupportResultFactory = convertSupportResultFactory;
        typeConverterList.forEach(typeConverter -> typeConverter.configTypeConverter(this));
        this.typeConverterList.addAll(typeConverterList);
        this.typeConverterList.add(new CollectionConverter.ArrayToCollectionTypeConverter(this, convertSupportResultFactory));
        this.typeConverterList.add(new CollectionConverter.CollectionTypeConverter(this, convertSupportResultFactory));
        this.typeConverterList.add(new CollectionConverter.CollectionToArrayTypeConverter(this, convertSupportResultFactory));
        this.typeConverterList.add(new CollectionConverter.CollectionToArrayTypeConverter(this, convertSupportResultFactory));
        this.typeConverterList.add(new DateConverter.CalendarToDateConverter(this, convertSupportResultFactory));
        this.typeConverterList.add(new DateConverter.NumberToDateConverter(this, convertSupportResultFactory));
        this.typeConverterList.add(new DateConverter.DateToLongConverter(this, convertSupportResultFactory));
        this.typeConverterList.add(new DateConverter.DateToCalendarConverter(this, convertSupportResultFactory));
        this.typeConverterList.add(new DateConverter.StringToDateConverter(this, convertSupportResultFactory));
        this.typeConverterList.add(new DateConverter.DateToStringConverter(this, convertSupportResultFactory));
        this.typeConverterList.add(new NumberConverter.NumberTypeConverter(this, convertSupportResultFactory));
        this.typeConverterList.add(new NumberConverter.StringToNumberConverter(this, convertSupportResultFactory));
        this.typeConverterList.add(new ObjectToStringConverter(this, convertSupportResultFactory));


    }

    protected TypeConverter findConverter(Tuple2<TypeWrapper, TypeWrapper> tuple2) {

        Optional<TypeConverter> result = typeConverterList.stream()
                .map(typeConverter1 -> typeConverter1.support(tuple2.getV1(), tuple2.getV2()))
                .filter(TypeConverter.ConvertSupportResult::isSupport)
                .min(TypeConverter.ConvertSupportResult.comparator)
                .map(TypeConverter.ConvertSupportResult::getTypeConverter);
        result.map(result1 -> typeConverterMap.put(tuple2, result1));
        return result.orElse(null);
    }

    protected TypeConverter getConverter(Tuple2<TypeWrapper, TypeWrapper> tuple2) {
        TypeConverter typeConverter = typeConverterMap.get(tuple2);
        if (typeConverter == null) {
            typeConverter = findConverter(tuple2);
        }
        return typeConverter;
    }

    public void register(TypeWrapper from, TypeWrapper to, TypeConverter typeConverter) {
        typeConverterMap.put(Tuple2.of(from, to), typeConverter);

    }

    public TypeConverter.ConvertSupportResult support(TypeWrapper fromType, TypeWrapper toType) {
        TypeConverter typeConverter = getConverter(Tuple2.of(fromType, toType));
        return typeConverter.support(fromType, toType);

    }


    public <T> T convert(Object value, TypeWrapper fromType, TypeWrapper toType) {
        if (toType.isSimpleType() && fromType.isSimpleType() && fromType.isRawClassParent(toType)) {
            return (T) value;
        }
        Tuple2<TypeWrapper, TypeWrapper> tuple2 = Tuple2.of(fromType, toType);
        return getConverter(tuple2).convert(value, fromType, toType);
    }


    public ConvertSupportResultFactoryInterface getConvertSupportResultFactory() {
        return convertSupportResultFactory;
    }
}
