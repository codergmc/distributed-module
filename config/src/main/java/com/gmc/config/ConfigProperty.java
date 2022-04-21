package com.gmc.config;

import java.util.Map;

public interface ConfigProperty extends Iterable<ConfigProperty.ConfigEntry> {
    <T> void put(ConfigKey<T> key, ConfigValue<T> value);

    <T> ConfigValue<T> getConfigValue(ConfigKey<T> configKey);

    default <T> T getConfig(ConfigKey<T> configKey) {
        ConfigValue<T> configValue = getConfigValue(configKey);
        if (configValue == null) {
            return null;
        }
        return configValue.getValue();
    }

    void mergeNewConfigProperty(ConfigProperty configProperty);

    <V> boolean addConfigListener(ConfigKey<V> configKey, ConfigListener<V> configListener);

    <V> boolean contain(ConfigKey<V> configKey);

    ConfigProperty get(int index);

    void add(int index, ConfigProperty configProperty);

    void add(ConfigProperty configProperty);

    class ConfigEntry {
        private ConfigKey<?> configKey;
        private ConfigValue<?> configValue;

        public ConfigEntry(ConfigKey<?> configKey, ConfigValue<?> configValue) {
            this.configKey = configKey;
            this.configValue = configValue;
        }

        public ConfigKey<?> getConfigKey() {
            return configKey;
        }

        public ConfigValue<?> getConfigValue() {
            return configValue;
        }
    }

    interface ConfigListener<T> {
        void notify(ConfigValue<T> oldValue, ConfigValue<T> newValue);

    }
}
