package com.gmc.config.convert;

import com.gmc.config.TypeWrapper;

import java.math.BigDecimal;
import java.math.BigInteger;

public class NumberConverter {
    public static class NumberTypeConverter extends InExactSimpleTypeConverter<Number, Number>{
        public NumberTypeConverter(TypeConverters typeConverters,ConvertSupportResultFactoryInterface convertSupportResultFactory) {
            super(typeConverters,convertSupportResultFactory);
        }

        @Override
        public <T> T convert0(Object value, TypeWrapper fromType, TypeWrapper toType) {
            Class<?> tClass = toType.getRawClass();
            if (Boolean.class.isAssignableFrom(tClass) || boolean.class.isAssignableFrom(tClass)) {
                if ("1".equals(value.toString())) {
                    return ((T) Boolean.TRUE);
                } else if ("0".equals(value.toString())) {
                    return (T) Boolean.FALSE;
                }
                throw new IllegalArgumentException();
            }
            if (Byte.class.isAssignableFrom(tClass) || byte.class.isAssignableFrom(tClass)) {
                return (T) Byte.valueOf(((Number) value).byteValue());
            }
            if (Short.class.isAssignableFrom(tClass) || short.class.isAssignableFrom(tClass)) {
                return (T) Short.valueOf(((Number) value).shortValue());
            }
            if (Integer.class.isAssignableFrom(tClass) || int.class.isAssignableFrom(tClass)) {
                return (T) Integer.valueOf(((Number) value).intValue());
            }
            if (Long.class.isAssignableFrom(tClass) || long.class.isAssignableFrom(tClass)) {
                return (T) Long.valueOf(((Number) value).longValue());
            }
            if (Float.class.isAssignableFrom(tClass) || float.class.isAssignableFrom(tClass)) {
                return (T) Float.valueOf(((Number) value).floatValue());
            }
            if (Double.class.isAssignableFrom(tClass) || Double.class.isAssignableFrom(tClass)) {
                return (T) Double.valueOf(((Number) value).doubleValue());
            }
            if (BigInteger.class.isAssignableFrom(tClass)) {
                return (T) new BigInteger(value.toString());
            }
            if (BigDecimal.class.isAssignableFrom(tClass)) {
                return (T) new BigDecimal(value.toString());
            }
            throw new IllegalArgumentException();
        }


    }

    public static class StringToNumberConverter extends InExactSimpleTypeConverter<String, Number> {
        public StringToNumberConverter(TypeConverters typeConverters,ConvertSupportResultFactoryInterface convertSupportResultFactory) {
            super(typeConverters,convertSupportResultFactory);
        }

        @Override
        public <T> T convert0(Object value, TypeWrapper fromType, TypeWrapper toType) {
            return (T) stringToNumber((String) value, toType.getRawClass());
        }


        protected <T> T stringToNumber(String value, Class<T> tClass) {
            if (Boolean.class.isAssignableFrom(tClass) || boolean.class.isAssignableFrom(tClass)) {
                if ("true".equalsIgnoreCase(value) || "1".equals(value)) {
                    return (T) Boolean.TRUE;
                }
                if ("false".equalsIgnoreCase(value) || "0".equals(value)) {
                    return (T) Boolean.FALSE;
                }
                throw new IllegalArgumentException();
            }
            if (Byte.class.isAssignableFrom(tClass) || byte.class.isAssignableFrom(tClass)) {
                return (T) Byte.valueOf(value);
            }
            if (Short.class.isAssignableFrom(tClass) || short.class.isAssignableFrom(tClass)) {
                return (T) Short.valueOf(value);
            }
            if (Integer.class.isAssignableFrom(tClass) || int.class.isAssignableFrom(tClass)) {
                return (T) Integer.valueOf(value);
            }
            if (Long.class.isAssignableFrom(tClass) || long.class.isAssignableFrom(tClass)) {
                return (T) Long.valueOf(value);
            }
            if (Float.class.isAssignableFrom(tClass) || float.class.isAssignableFrom(tClass)) {
                return (T) Float.valueOf(value);
            }
            if (Double.class.isAssignableFrom(tClass) || double.class.isAssignableFrom(tClass)) {
                return (T) Double.valueOf(value);
            }
            if (BigInteger.class.isAssignableFrom(tClass)) {
                return (T) new BigInteger(value);
            }
            if (BigDecimal.class.isAssignableFrom(tClass)) {
                return (T) new BigDecimal(value);
            }
            throw new IllegalArgumentException();

        }
    }
}
