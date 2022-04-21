package com.gmc.core;


import javax.validation.constraints.NotNull;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReflectUtils {
    /**
     * get the type of {@code parent} child,Follow the parent class of {@code type}
     *
     * @param type
     * @param parent
     * @return null if {@code type} is not the child of {@code parent}
     */
    public static Type getTypeChild(Type type, Class<?> parent) {
        if (type instanceof Class) {
            Type genericSuperclass = ((Class<?>) type).getGenericSuperclass();
            if (genericSuperclass == null) {
                return null;
            }
            if (getRowClass(genericSuperclass).equals(parent)) {
                return genericSuperclass;
            }
            return getTypeChild(genericSuperclass, parent);
        } else if (type instanceof ParameterizedType) {
            Class<?> rowClass = getRowClass(type);
            if (rowClass.equals(parent)) {
                return type;
            }
            return getTypeChild(getRowClass(type), parent);
        } else throw new IllegalArgumentException();
    }

    /**
     * get the distance of class inheritance hierarchy, {@code c} is always the parent of {@code type}
     *
     * @param type
     * @param c
     * @return 0 if {@code type} and {@code c} represent the same class, -1 if {@code c} is not  the parent of {@code type}
     */
    public static int rowClassDistance(Type type, Class<?> c) {
        Class<?> rowClass = getRowClass(type);
        if (rowClass.equals(c)) {
            return 0;
        }
        if (c.isAssignableFrom(rowClass)) {
            List<Type> directParent = getDirectParent(rowClass);
            return directParent.stream().map(parentType -> rowClassDistance(parentType, c))
                    .filter(distance -> distance > 0)
                    .min(Integer::compareTo)
                    .map(distance -> distance + 1)
                    .orElse(-1);
        } else {
            return -1;
        }
    }


    @NotNull
    public static List<Type> getDirectParent(Type type) {
        List<Type> result = new ArrayList<>(1);
        Class<?> rowClass = getRowClass(type);
        Type genericSuperclass = rowClass.getGenericSuperclass();
        if (genericSuperclass != null) {
            result.add(genericSuperclass);
        }
        Type[] genericInterfaces = rowClass.getGenericInterfaces();
        if (genericInterfaces.length > 0) {
            result.addAll(Arrays.asList(genericInterfaces));
        }
        return result;
    }

    /**
     * {@code c} is child of {@code type}
     *
     * @param type
     * @param c
     * @return
     */
    public static boolean rowClassChild(Type type, Class<?> c) {
        if (rowClassDistance(c, getRowClass(type)) == 1) {
            return true;
        }
        return false;
    }

    /**
     * {@code c} is parent of {@code type}
     *
     * @param type
     * @param c
     * @return
     */
    public static boolean rowClassParent(Type type, Class<?> c) {
        return rowClassDistance(type, c) == 1;
    }

    public static boolean rowClassEqual(Type type, Class<?> c) {
        return getRowClass(type).equals(c);
    }


    public static Class<?> getRowClass(Type type) {
        assert type != null;
        if (type instanceof Class) {
            return (Class<?>) type;
        } else if (type instanceof ParameterizedType) {
            return getRowClass(((ParameterizedType) type).getRawType());
        } else throw new IllegalArgumentException();

    }

    public static Type getComponentType(Type type, int index) {
        assert index >= 0;
        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
            if (actualTypeArguments.length <= index)
                throw new IllegalArgumentException(LogUtils.format("type:{} , index:{},  out of order", type, index));
            return actualTypeArguments[index];
        }
        if (type instanceof Class) {
            Class<?> aClass = (Class<?>) type;
            if (aClass.isArray()) {
                return aClass.getComponentType();
            }
        }
        throw new IllegalArgumentException(LogUtils.format("type:{} ,index:{}", type, index));

    }


}
