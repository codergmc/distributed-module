package com.gmc.config;

import com.gmc.core.LogUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class BeanUtils {
    public static boolean isJavaBean(TypeWrapper typeWrapper) {
        try {
            processJavaBean(typeWrapper, (a,b,c)->{});
            return true;
        } catch (NotJavaBeanException e) {
            return false;
        }

    }

    public static void processJavaBean(TypeWrapper typeWrapper, CiConsumer<Field, Method, Method> ciConsumer) throws NotJavaBeanException {
        Class<?> aClass = typeWrapper.getRawClass();
        if (aClass.getPackageName().startsWith("java")) {
            throwNotJavaBeanException(aClass);
        }
        try {
            Constructor<?> declaredConstructor = aClass.getDeclaredConstructor();
        } catch (Exception e) {
            throwNotJavaBeanException(aClass);
        }
        while (aClass != null && !aClass.equals(Object.class)) {
            Field[] fields = aClass.getDeclaredFields();
            for (Field field : fields) {
                if (!Modifier.isStatic(field.getModifiers())) {
                    String name = field.getName();
                    String getMethodName = getMethodName(field);
                    String setMethodName = setMethodName(field);
                    try {
                        Method getMethod = getMethod(getMethodName, aClass);
                        Method setMethod = getMethod(setMethodName, aClass, field.getType());
                        ciConsumer.accept(field, getMethod, setMethod);
                    } catch (Exception e) {
                        throwNotJavaBeanException(aClass);
                    }
                }
                aClass = aClass.getSuperclass();

            }

        }
    }


    private static void throwNotJavaBeanException(Class<?> aClass) throws NotJavaBeanException {
        throw new NotJavaBeanException(LogUtils.format("class not java bean:{}", aClass));
    }

    private static Method getMethod(String methodName, Class<?> c, Class<?>... paramTypes) throws NoSuchMethodException {
        return c.getMethod(methodName, paramTypes);
    }

    private static String setMethodName(Field field) {
        String name = field.getName();
        String setMethodName = "set" + Character.toUpperCase(name.charAt(0)) + name.substring(1);
        return setMethodName;
    }

    private static String getMethodName(Field field) {
        String fieldName = field.getName();
        String getMethodName;
        if (field.getType().equals(boolean.class) || field.getType().equals(Boolean.class)) {
            getMethodName = "is" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);

        } else {
            getMethodName = "get" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
        }
        return getMethodName;
    }
}
