package com.gmc.config.convert;

import com.gmc.config.InstanceFactory;
import com.gmc.config.ObjectTypeWrapper;
import com.gmc.config.TypeWrapper;

import java.lang.reflect.Array;
import java.util.Collection;

public class CollectionConverter {


    public static class CollectionTypeConverter extends CompositeTypeConverter {


        public CollectionTypeConverter(TypeConverters typeConverters, ConvertSupportResultFactoryInterface convertSupportResultFactory) {
            super(typeConverters, convertSupportResultFactory);
        }

        @Override
        protected TypeWrapper getType(Object value, TypeWrapper typeWrapper) {
            Collection<?> collection = (Collection<?>) value;
            if (collection.size() > 0) {
                Object next = collection.iterator().next();
                return new ObjectTypeWrapper(typeWrapper, next);
            }
            return new ObjectTypeWrapper(typeWrapper);
        }

        @Override
        protected ConvertSupportResult checkRowClass(TypeWrapper fromType, TypeWrapper toType) {
            return convertSupportResultFactory.merge(convertSupportResultFactory.createSimpleType(fromType, TypeWrapper.of(Collection.class),typeConverters.get()), convertSupportResultFactory.createSimpleType(toType, TypeWrapper.of(Collection.class),typeConverters.get())).setTypeConverter(this);
        }

        @Override
        protected ConvertSupportResult checkComponent(TypeWrapper fromType, TypeWrapper toType) {
            return convertSupportResultFactory.createComponent(fromType,toType,typeConverters.get());
        }

        @Override
        public <T> T convert(Object value, TypeWrapper fromType, TypeWrapper toType) {
            Collection<?> to = InstanceFactory.create(toType.getRawClass());
            Collection<?> from = (Collection<?>)  value;
            for (Object o : from) {
                to.add(typeConverters.get().convert(o, fromType.getComponentType(0), toType.getComponentType(0)));
            }
            return (T) to;

        }


    }


    public static class ArrayToCollectionTypeConverter extends CompositeTypeConverter {


        public ArrayToCollectionTypeConverter(TypeConverters typeConverters, ConvertSupportResultFactoryInterface convertSupportResultFactory) {
            super(typeConverters, convertSupportResultFactory);
        }

        @Override
        protected TypeWrapper getType(Object value, TypeWrapper typeWrapper) {
            throw new IllegalArgumentException();
        }

        @Override
        protected ConvertSupportResult checkRowClass(TypeWrapper fromType, TypeWrapper toType) {
            return convertSupportResultFactory.merge(convertSupportResultFactory.createArray(fromType), convertSupportResultFactory.createSimpleType(toType, TypeWrapper.of(Collection.class), typeConverters.get())).setTypeConverter(this);
        }

        @Override
        protected ConvertSupportResult checkComponent(TypeWrapper fromType, TypeWrapper toType) {
            return convertSupportResultFactory.createComponent(fromType,toType,typeConverters.get());
        }

        @Override
        public <T> T convert(Object value, TypeWrapper fromType, TypeWrapper toType) {
            int length = Array.getLength(value);
            Collection<?> collection = InstanceFactory.create(toType.getRawClass());
            for (int i = 0; i < length; i++) {
                collection.add(typeConverters.get().convert(Array.get(value, i), fromType.getSingleComponentType(), toType.getSingleComponentType()));
            }
            return (T) collection;
        }
    }

    public static class CollectionToArrayTypeConverter extends CompositeTypeConverter {

        public CollectionToArrayTypeConverter(TypeConverters typeConverters, ConvertSupportResultFactoryInterface convertSupportResultFactory) {
            super(typeConverters, convertSupportResultFactory);
        }

        @Override
        protected TypeWrapper getType(Object value, TypeWrapper typeWrapper) {
            Collection<?> collection = (Collection<?>) value;
            if (collection.size() > 0) {
                Object o = collection.iterator().next();
                return new ObjectTypeWrapper(typeWrapper, o);
            }
            return typeWrapper;
        }

        @Override
        protected ConvertSupportResult checkRowClass(TypeWrapper fromType, TypeWrapper toType) {
            return convertSupportResultFactory.merge(convertSupportResultFactory.createSimpleType(fromType, TypeWrapper.of(Collection.class),typeConverters.get()), convertSupportResultFactory.createArray(toType)).setTypeConverter(this);
        }

        @Override
        protected ConvertSupportResult checkComponent(TypeWrapper fromType, TypeWrapper toType) {
            return convertSupportResultFactory.createComponent(fromType,toType,typeConverters.get());
        }




        @Override
        public <T> T convert(Object value, TypeWrapper fromType, TypeWrapper toType) {
            Collection<?> collection = (Collection<?>) value;

            int length = collection.size();
            TypeWrapper componentType = toType.getSingleComponentType();
            Object array = Array.newInstance(componentType.getRawClass(), length);
            int i = 0;
            for (Object element : collection) {
                Array.set(array, i, typeConverters.get().convert(element, fromType.getSingleComponentType(), componentType));
                i++;
            }
            return (T) array;
        }
    }
}
