package com.gmc.config;

import com.gmc.config.convert.TypeConverter;
import com.gmc.config.convert.TypeConverters;

import java.beans.IndexedPropertyDescriptor;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

public class ObjectTypeWrapper extends TypeWrapper {
    private List<?> elements;

    public ObjectTypeWrapper(TypeWrapper ori, Object... elements) {
        super(ori);
        this.elements = Arrays.asList(elements);
    }

    public List<?> getElements() {
        return elements;
    }

    @Override
    public TypeWrapper getComponentType(int index) {
        assert index >= 0 && index < elements.size();
        return TypeWrapper.of(getElements().get(index).getClass());
    }

    @Override
    public int getComponentTypeSize() {
        return elements.size();
    }

    @Override
    public TypeConverter.ConvertSupportResult canComponentConvert(TypeWrapper wrapper, TypeConverters typeConverters) {
        if (elements.size() > 0) {
            return super.canComponentConvert(wrapper, typeConverters);
        } else {
            return typeConverters.getConvertSupportResultFactory().createHighestPriority();
        }
    }
}