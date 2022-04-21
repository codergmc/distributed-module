package com.gmc.config;

import com.gmc.config.convert.TypeConverter;
import com.gmc.config.convert.TypeConverters;
import com.gmc.core.LogUtils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TypeWrapper {
    public static TypeWrapper EMPTY = new TypeWrapper();
    public static Map<Class<?>, Class<?>> primitiveToWrap = new HashMap();

    static {
        primitiveToWrap.put(boolean.class, Boolean.class);
        primitiveToWrap.put(char.class, Character.class);
        primitiveToWrap.put(byte.class, Byte.class);
        primitiveToWrap.put(short.class, Short.class);
        primitiveToWrap.put(int.class, Integer.class);
        primitiveToWrap.put(float.class, Float.class);
        primitiveToWrap.put(long.class, Long.class);
        primitiveToWrap.put(double.class, Double.class);
        primitiveToWrap.put(void.class, Void.class);
    }

    private Type type;
    private Class<?> rawClass;

    public TypeWrapper(TypeWrapper typeWrapper) {
        this.type = typeWrapper.getType();
        this.rawClass = typeWrapper.getRawClass();
    }

    private TypeWrapper() {
        IntStream.range(1,10).boxed().collect(Collectors.toSet());
    }

    public TypeWrapper(Type type) {
        if (type instanceof Class) {
            if (((Class<?>) type).isArray()) {
                this.type = type;
                this.rawClass = (Class<?>) type;
                return;
            } else {
                this.rawClass = wrapPrimitive((Class<?>) type);
                return;
            }
        } else if (type instanceof ParameterizedType) {
            this.type = type;
            if (((ParameterizedType) type).getRawType() instanceof Class) {
                this.rawClass = (Class<?>) ((ParameterizedType) type).getRawType();
                return;
            }
        }
        throw new IllegalArgumentException(LogUtils.format("un support type:{}", type));

    }


    public static TypeWrapper of(TypeReference<?> typeReference) {
        return of(typeReference.getType());
    }

    public static TypeWrapper of(Type type) {
        return new TypeWrapper(type);
    }

    public static TypeWrapper of(Class<?> aClass) {
        return new TypeWrapper(aClass);
    }

    public boolean isSimpleType() {
        return rawClass != null && type == null && !isArray()&&!BeanUtils.isJavaBean(this);
    }

    public boolean isCompositeType() {
        return type != null || isArray()||BeanUtils.isJavaBean(this);
    }

    public Type getType() {
        return type;
    }

    public Class<?> getRawClass() {
        return rawClass;
    }

    public TypeWrapper getRawType() {
        return TypeWrapper.of(rawClass);
    }

    public static Class<?> wrapPrimitive(Class<?> primitive) {
        Class<?> wrapClass = primitiveToWrap.get(primitive);
        if (wrapClass == null) {
            return primitive;
        }
        return wrapClass;
    }

    public boolean isArray() {
        return rawClass.isArray();
    }


    public TypeWrapper getSingleComponentType() {
        assert isCompositeType();
        assert getComponentTypeSize() == 1;
        return getComponentType(0);

    }

    public TypeWrapper getComponentType(int index) {
        assert isCompositeType();
        if (type instanceof ParameterizedType) {
            Type[] actualTypeArguments = ((ParameterizedType) type).getActualTypeArguments();
            assert actualTypeArguments.length > index;
            return TypeWrapper.of(actualTypeArguments[index]);
        } else if (type instanceof Class) {
            if (((Class<?>) type).isArray()) {
                if(index!=0){
                    throw new IllegalArgumentException();
                }
                return TypeWrapper.of(((Class<?>) type).getComponentType());
            }
        }
        throw new IllegalArgumentException();

    }

    public int getComponentTypeSize() {
        assert isCompositeType();
        if (type instanceof ParameterizedType) {
            return ((ParameterizedType) type).getActualTypeArguments().length;
        } else if (type instanceof Class) {
            if (((Class<?>) type).isArray()) {
                return 1;
            }
        }
        throw new IllegalArgumentException();

    }

    public boolean isRowClassInstance(Object value) {
        if (this.rawClass.isInstance(value))
            return true;
        return false;


    }

    public boolean notEnoughComposite() {
        assert isCompositeType();
        if (type == null) {
            return true;
        }
        return false;
    }

    public boolean isRawClassParent(TypeWrapper parent) {
        return parent.getRawClass().isAssignableFrom(this.rawClass);
    }

    public TypeConverter.ConvertSupportResult canConvert(TypeWrapper wrapper, TypeConverters typeConverters) {
        if (isSimpleType()) {
            if (isRawClassParent(wrapper)) {
                return typeConverters.getConvertSupportResultFactory().createSimpleType(this, wrapper, typeConverters);
            }
            return typeConverters.support(this, wrapper);
        } else if (isCompositeType()) {
            TypeConverter.ConvertSupportResult convertSupportResult = this.getRawType().canConvert(wrapper.getRawType(), typeConverters);
            if (convertSupportResult.isSupport()) {
                TypeConverter.ConvertSupportResult convertSupportResult1 = typeConverters.getConvertSupportResultFactory().createComponent(this, wrapper, typeConverters);
                convertSupportResult = typeConverters.getConvertSupportResultFactory().mergeChild(convertSupportResult, convertSupportResult1);
            }
            return convertSupportResult;

        }
        throw new IllegalArgumentException();
    }

    public TypeConverter.ConvertSupportResult canComponentConvert(TypeWrapper wrapper, TypeConverters typeConverters) {
        assert isCompositeType();
        TypeConverter.ConvertSupportResult result = null;
        if (getComponentTypeSize() == wrapper.getComponentTypeSize()) {
            for (int i = 0; i < getComponentTypeSize(); i++) {
                TypeWrapper componentType = getComponentType(i);
                TypeWrapper other = wrapper.getComponentType(i);
                TypeConverter.ConvertSupportResult convertSupportResult = typeConverters.getConvertSupportResultFactory().createCompositeType(componentType, other, typeConverters);
                if (result == null) {
                    result = convertSupportResult;
                } else {
                    result = typeConverters.getConvertSupportResultFactory().merge(result, convertSupportResult);
                }
                if (!result.isSupport()) {
                    break;
                }

            }

        }
        return result;
    }

    public boolean isRawClassChild(TypeWrapper wrapper) {
        return this.getRawClass().isAssignableFrom(wrapper.getRawClass());
    }


    public TypeConverter.ConvertSupportResult getRowClassDistance(TypeWrapper parent, TypeConverters typeConverters) {
        if (isArray()) {
            return typeConverters.getConvertSupportResultFactory().createArray(parent);
        }
        if (parent.isArray()) {
            return typeConverters.getConvertSupportResultFactory().createArray(this);
        }
        if (!this.isRawClassParent(parent)) {
            return TypeConverter.ConvertSupportResult.ofUnSupport();
        } else {
            return TypeConverter.ConvertSupportResult.of(getRawTypeDistance(this, parent, 0));
        }

    }

    public static TypeConverter.ConvertSupportResult getRowClassArrayDistance() {
        return TypeConverter.ConvertSupportResult.of(0);
    }

    public List<TypeWrapper> getDirectParent() {
        Class<?> superclass = rawClass.getSuperclass();
        if (superclass == null) {
            Class<?>[] interfaces = rawClass.getInterfaces();
            if (interfaces.length == 0) {
                return Collections.EMPTY_LIST;
            }
            return Arrays.stream(interfaces).map(TypeWrapper::of).collect(Collectors.toList());
        }
        return Collections.singletonList(TypeWrapper.of(superclass));
    }

    private static int getRawTypeDistance(TypeWrapper from, TypeWrapper to, int distance) {
        if (!from.isRawClassParent(to)) {
            return -1;
        }
        if (from.getRawType().equals(to.getRawType())) {
            return distance;
        }
        List<TypeWrapper> directParent = from.getDirectParent();
        return directParent.stream().map(type -> getRawTypeDistance(type, to, distance + 1))
                .filter(integer -> integer > 0)
                .min(Comparator.comparingInt(o -> o))
                .orElse(-1);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TypeWrapper that = (TypeWrapper) o;

        if (type != null ? !type.equals(that.type) : that.type != null) return false;
        return rawClass != null ? rawClass.equals(that.rawClass) : that.rawClass == null;
    }


    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + (rawClass != null ? rawClass.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "TypeWrapper{" +
                "type=" + type +
                ", rawClass=" + rawClass +
                '}';
    }

}
