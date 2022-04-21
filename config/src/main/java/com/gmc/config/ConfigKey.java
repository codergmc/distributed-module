package com.gmc.config;

import com.gmc.config.convert.TypeConverters;
import com.gmc.config.validation.Verifier;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ConfigKey<T> {
    private String key;
    private String describe;
    private TypeWrapper valueType;
    private Verifier<T> verifier;
    private T defaultValue;
    private TypeConverters typeConverters;

    public ConfigKey(String key, String describe, TypeReference<T> valueType, Verifier<T> verifier, T defaultValue) {
        assert key != null && key.length() > 0 && validateKey(key);
        assert valueType != null;
        this.key = key;
        this.describe = describe;
        this.valueType = TypeWrapper.of(valueType.getType());
        this.verifier = verifier;
        this.defaultValue = defaultValue;
        if (this.verifier != null && this.defaultValue != null) {
            try {
                verifier.validate(defaultValue);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static boolean validateKey(String key) {
        return key.matches("[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*");
    }

    public ConfigValue<T> newConfigValue() {
        return new ConfigValue<T>(this
                , ConfigMidValue.create(valueType, typeConverters, false));
    }

    public ConfigValue<T> newDefaultConfigValue() {
        ConfigValue<T> value = new ConfigValue<T>(this
        );
        value.setValue(this.getDefaultValue());
        return value;
    }


    public void setting(TypeConverters typeConverters) {
        this.typeConverters = typeConverters;
    }

    public ConfigValue<T> parse(Object object) {
        T cast = typeConverters.convert(object, TypeWrapper.of(object.getClass()), TypeWrapper.of(valueType.getType()));
        ConfigValue<T> value = new ConfigValue<>(this
                , cast);
        return value;
    }

    public T getDefaultValue() {
        return defaultValue;
    }

    public ConfigValue<T> getDefaultConfigValue() {
        return new ConfigValue<T>(this, defaultValue);
    }

    public String getKey() {
        return key;
    }

    public List<String> getKeyParent() {
        int index = 0, lastIndex = 0;
        List<String> result = new ArrayList<>();
        while ((index = key.indexOf(".", lastIndex)) > 0) {
            result.add(key.substring(0, index));
            lastIndex = index + 1;
        }
        return result;
    }

    public String getDescribe() {
        return describe;
    }

    public TypeWrapper getValueType() {
        return valueType;
    }

    public Verifier<T> getVerifier() {
        return verifier;
    }

    public TypeConverters getTypeConverters() {
        return typeConverters;
    }

    public static class ConfigKeyBuilder<T> {
        String key;
        String describe;
        TypeReference<T> valueType;
        Verifier<T> verifier;
        T defaultValue;

        private ConfigKeyBuilder(String key, TypeReference<T> valueType) {
            this.key = key;
            this.valueType = valueType;
        }


        public static <T> ConfigKeyBuilder<T> builder(String key, TypeReference<T> valueType) {
            return new ConfigKeyBuilder<>(key, valueType);
        }

        public ConfigKeyBuilder<T> key(String key) {
            this.key = key;
            return this;
        }

        public ConfigKeyBuilder<T> describe(String describe) {
            this.describe = describe;
            return this;
        }

        public ConfigKeyBuilder<T> defaultValue(T defaultValue) {
            this.defaultValue = defaultValue;
            return this;
        }

        public ConfigKeyBuilder<T> defaultValue(Supplier<T> supplier) {
            this.defaultValue = supplier.get();
            return this;
        }

        public ConfigKeyBuilder<T> valueType(TypeReference<T> valueType) {
            this.valueType = valueType;
            return this;
        }

        public ConfigKeyBuilder<T> verifier(Verifier<T> verifier) {
            this.verifier = verifier;
            return this;
        }

        public ConfigKey<T> build() {
            return new ConfigKey<>(key, describe, valueType, verifier, defaultValue);
        }
    }


}
