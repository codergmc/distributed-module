package com.gmc.config;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class ConfigProperties implements ConfigProperty {
    private final List<ConfigProperty> propertyList = new ArrayList<>();


    public void addConfigProperty(ConfigProperty configProperty) {
        propertyList.add(configProperty);
    }


    @Override
    public <T> void put(ConfigKey<T> key, ConfigValue<T> value) {
        throw new UnsupportedOperationException();
    }


    @Override
    public <T> ConfigValue<T> getConfigValue(ConfigKey<T> configKey) {
        return propertyList.stream().map(property -> property.getConfigValue(configKey))
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }

    @Override
    public void mergeNewConfigProperty(ConfigProperty configProperty) {
        throw new UnsupportedOperationException();
    }


    @Override
    public <V> boolean addConfigListener(ConfigKey<V> configKey, ConfigListener<V> configListener) {
        return propertyList.stream().anyMatch(configProperty -> configProperty.addConfigListener(configKey, configListener));
    }

    @Override
    public <V> boolean contain(ConfigKey<V> configKey) {
        return propertyList.stream().anyMatch(configProperty -> configProperty.contain(configKey));
    }

    @Override
    public ConfigProperty get(int index) {
        assert index >= 0 && index < propertyList.size();
        return propertyList.get(index);
    }

    @Override
    public void add(int index, ConfigProperty configProperty) {
        propertyList.add(index, configProperty);
    }

    @Override
    public void add(ConfigProperty configProperty) {
        propertyList.add(configProperty);
    }


    @Override
    public Iterator<ConfigEntry> iterator() {
        return new ConfigPropertiesIterator();
    }

    class ConfigPropertiesIterator implements Iterator<ConfigEntry> {
        Iterator<ConfigProperty> iterator = propertyList.iterator();
        Iterator<ConfigEntry> configEntryIterable;
        ConfigEntry next;

        @Override
        public boolean hasNext() {
            if (configEntryIterable == null) {
                if (!iterator.hasNext()) {
                    return false;
                } else {
                    configEntryIterable = iterator.next().iterator();
                    return hasNext();
                }
            }
            if (configEntryIterable.hasNext()) {
                next = configEntryIterable.next();
                return true;
            } else {
                configEntryIterable = null;
                return hasNext();
            }
        }

        @Override
        public ConfigEntry next() {
            return next;
        }
    }
}
