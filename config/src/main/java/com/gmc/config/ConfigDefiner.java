package com.gmc.config;

import com.gmc.config.ConfigKey;
import com.gmc.config.convert.TypeConverter;
import com.gmc.config.convert.TypeConverters;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigDefiner {
    private List<ConfigKey<?>> configKeyList;
    private Map<String, ConfigKey<?>> configKeyMap = new HashMap<>();
    private Map<String, List<ConfigKey<?>>> parentPath2Key = new HashMap<>();
    private TypeConverters typeConverters;

    public ConfigDefiner(TypeConverters typeConverters, Class<?>... classes) throws IllegalAccessException {
        this(typeConverters, Arrays.asList(classes));

    }

    public List<ConfigKey<?>> getConfigKeyList() {
        return configKeyList;
    }

    public boolean containPath(String path) {
        return parentPath2Key.containsKey(path);
    }

    public ConfigKey<?> getConfigKey(String key) {
        return configKeyMap.get(key);

    }

    public ConfigKey<?> findMatchConfigKey(String key) {
        ConfigKey<?> configKey = getConfigKey(key);
        if (configKey == null) {
            int index = key.lastIndexOf(".");
            if (index < 0) {
                return null;
            } else {
                return findMatchConfigKey(key.substring(0, index));
            }

        } else {
            return configKey;
        }

    }

    public ConfigDefiner(TypeConverters typeConverters, List<Class<?>> classList) throws IllegalAccessException {
        assert typeConverters != null;
        assert classList != null;
        this.typeConverters = typeConverters;
        this.configKeyList = defineAllConfig(classList);
        configKeyList.forEach(configKey -> configKey.setting(typeConverters));
        configKeyList.forEach(configKey -> configKeyMap.put(configKey.getKey(), configKey));
        configKeyList.forEach(configKey -> {
            for (String key : configKey.getKeyParent()) {
                parentPath2Key.computeIfAbsent(key, k -> new ArrayList<>()).add(configKey);
            }
        });
    }

    private List<ConfigKey<?>> defineAllConfig(List<Class<?>> classList) throws IllegalAccessException {
        assert classList != null;
        List<ConfigKey<?>> configKeyList = new ArrayList<>();
        for (Class<?> aClass : classList) {
            Field[] declaredFields = aClass.getDeclaredFields();
            for (Field field : declaredFields) {
                field.setAccessible(true);
                int modifiers = field.getModifiers();
                if (Modifier.isFinal(modifiers) && Modifier.isStatic(modifiers) && Modifier.isPublic(modifiers) && field.getType().equals(ConfigKey.class)) {
                    ConfigKey<?> key = (ConfigKey<?>) field.get(null);
                    configKeyList.add(key);
                }
            }
        }
        return configKeyList;

    }
}
