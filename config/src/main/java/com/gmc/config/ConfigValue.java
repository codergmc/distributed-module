package com.gmc.config;

import java.util.ArrayList;
import java.util.List;

public class ConfigValue<T> {
    private ConfigKey<T> configKey;
    private T value;
    private ConfigMidValue configMidValue;
    private boolean manualGenerate = false;
    private List<ManualGenerateValueListener> listeners;

    public ConfigValue(ConfigKey<T> configKey) {
        this.configKey = configKey;
    }

    public ConfigValue(ConfigKey<T> configKey, T value) {
        this.configKey = configKey;
        this.value = value;
    }

    public ConfigValue(ConfigKey<T> configKey, ConfigMidValue configMidValue) {
        this.configKey = configKey;
        this.configMidValue = configMidValue;
    }

    public void generateValue() {
        if (!manualGenerate) {
            initValue(configMidValue.generate());
        }
    }

    public boolean isManualGenerate() {
        return manualGenerate;
    }

    public ConfigValue<T> setManualGenerate(boolean manualGenerate) {
        this.manualGenerate = manualGenerate;
        if(manualGenerate){
            listeners = new ArrayList<>();
        }
        ((AbstractConfigMidValue) configMidValue).setLazyMerge(manualGenerate);
        return this;
    }

    private void initValue(Object value) {
        this.value = (T) value;
        configMidValue = null;
        if (value == null) {
            this.value = configKey.getDefaultValue();
        }
    }

    public void addManualGenerateValueListener(ManualGenerateValueListener listener) {
        listeners.add(listener);
    }

    public void manualGenerateValue() {
        assert configMidValue != null;
        assert manualGenerate;
        initValue(configMidValue.generate());
        notifyManualGenerateValueListener();
    }

    private void notifyManualGenerateValueListener() {
        for (ManualGenerateValueListener listener : listeners) {
            listener.valueGenerateNotify();
        }
        listeners = null;
    }

    public void mergeMidValue(String key, Object value) {
        configMidValue.merge(configKey, key, value);
    }

    public ConfigValue<T> setValue(T value) {
        this.value = value;
        return this;
    }

    public ConfigKey<T> getConfigKey() {
        return configKey;
    }

    public T getValue() {
        return value;
    }

    public interface ManualGenerateValueListener {
        void valueGenerateNotify();

    }
}
