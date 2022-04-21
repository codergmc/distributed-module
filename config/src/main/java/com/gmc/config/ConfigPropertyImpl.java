package com.gmc.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledExecutorService;

public class ConfigPropertyImpl implements ConfigProperty {
    protected Map<ConfigKey<?>, ConfigValue<?>> configMap = new HashMap<>();
    protected Map<ConfigKey<?>, List<ConfigListener<?>>> listenerMap = new ConcurrentHashMap<>();

    @Override
    public <T> void put(ConfigKey<T> key, ConfigValue<T> value) {
        configMap.put(key, value);
    }

    @Override
    public <T> ConfigValue<T> getConfigValue(ConfigKey<T> configKey) {
        return (ConfigValue<T>) configMap.get(configKey);
    }

    @Override
    public void mergeNewConfigProperty(ConfigProperty configProperty) {
        Iterator<ConfigEntry> iterator = configProperty.iterator();
        while (iterator.hasNext()) {
            ConfigEntry next = iterator.next();
            ConfigKey<?> key = next.getConfigKey();
            ConfigValue<?> newValue = next.getConfigValue();
            ConfigValue<?> oldValue = configMap.get(key);
            if (oldValue == null && newValue != null) {
                configMap.put(key, newValue);
                checkListener(key,oldValue,newValue);
                continue;
            }
            if(oldValue!=null&&newValue==null){
                configMap.remove(key);
                checkListener(key,oldValue,newValue);
                continue;
            }
            if(oldValue==null&&newValue==null){
                continue;
            }
            if(!oldValue.equals(newValue)){
                configMap.put(key, newValue);
                checkListener(key,oldValue,newValue);
                continue;
            }

        }
    }

    protected void checkListener(ConfigKey key, ConfigValue oldValue, ConfigValue newValue) {
        List<ConfigListener<?>> configListeners = listenerMap.get(key);
        if (configListeners != null) {
            ScheduledExecutorService executorService = GlobalParams.INSTANCE.getParam(Config.EXECUTORS);
            executorService.execute(() -> configListeners.forEach(listener -> listener.notify(oldValue, newValue)));

        }

    }

    @Override
    public <V> boolean addConfigListener(ConfigKey<V> configKey, ConfigListener<V> configListener) {
        if (contain(configKey)) {
            List<ConfigListener<?>> listeners = listenerMap.computeIfAbsent(configKey, key -> new CopyOnWriteArrayList<>());
            listeners.add(configListener);
            return true;
        }
        return false;
    }


    @Override
    public <V> boolean contain(ConfigKey<V> configKey) {
        return configMap.containsKey(configKey);
    }

    @Override
    public ConfigProperty get(int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void add(int index, ConfigProperty configProperty) {
        throw new UnsupportedOperationException();

    }

    @Override
    public void add(ConfigProperty configProperty) {
        throw new UnsupportedOperationException();
    }


    @Override
    public Iterator<ConfigEntry> iterator() {
        Iterator<Map.Entry<ConfigKey<?>, ConfigValue<?>>> iterator = configMap.entrySet().iterator();
        return new Iterator<ConfigEntry>() {
            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public ConfigEntry next() {
                Map.Entry<ConfigKey<?>, ConfigValue<?>> next = iterator.next();
                return new ConfigEntry(next.getKey(), next.getValue());
            }
        };
    }
}
