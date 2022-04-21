package com.gmc.config.process;

import com.gmc.config.ConfigKey;
import com.gmc.config.ConfigProperty;
import com.gmc.config.ConfigValue;

public class OneConfigEntryProcessContext {
    private ConfigKey<?> configKey;
    private ConfigValue<?> configValue;
    private Object value;
    private ConfigProperty configProperty;

    /**
     *
     * @param configKey
     * @param configValue
     * @param value is null when in {@link OneConfigEntryPostProcessor},not null when in {@link OneConfigEntryPreProcessor}
     * @param configProperty
     */
    public OneConfigEntryProcessContext(ConfigKey<?> configKey, ConfigValue<?> configValue, Object value, ConfigProperty configProperty) {
        this.configKey = configKey;
        this.configValue = configValue;
        this.value = value;
        this.configProperty = configProperty;
    }

    public static OneConfigEntryProcessContext create(ConfigKey<?> configKey, ConfigValue<?> configValue, Object object, ConfigProperty configProperty) {
        return new OneConfigEntryProcessContext(configKey, configValue, object, configProperty);
    }

    public ConfigKey<?> getConfigKey() {
        return configKey;
    }

    public ConfigValue<?> getConfigValue() {
        return configValue;
    }

    public Object getValue() {
        return value;
    }

    public ConfigProperty getConfigProperty() {
        return configProperty;
    }
}
