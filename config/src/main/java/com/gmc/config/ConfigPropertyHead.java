package com.gmc.config;

import java.util.Iterator;

public class ConfigPropertyHead extends HierarchyConfigProperty implements ConfigProperty {

    public ConfigPropertyHead() {
        super(null);
    }

    @Override
    public <T> void put(ConfigKey<T> key, ConfigValue<T> value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> ConfigValue<T> getConfigValue(ConfigKey<T> configKey) {
        return child.getConfigValue(configKey);
    }

    @Override
    public void mergeNewConfigProperty(ConfigProperty configProperty) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <V> boolean addConfigListener(ConfigKey<V> configKey, ConfigListener<V> configListener) {
        return child.addConfigListener(configKey, configListener);
    }

    @Override
    public <V> boolean contain(ConfigKey<V> configKey) {
        if (child == null)
            return false;
        return child.contain(configKey);
    }

    @Override
    public ConfigProperty get(int index) {
        return child.get(index);
    }

    @Override
    public void add(int index, ConfigProperty configProperty) {
        if (index == 0) {
            child = (HierarchyConfigProperty) configProperty;
            child.setParent(this);
        }
        child.add(index, configProperty);
    }

    @Override
    public void add(ConfigProperty configProperty) {
        if (child == null) {
            child = (HierarchyConfigProperty) configProperty;
            child.setParent(this);
        }
        child.add(configProperty);
    }

    @Override
    public Iterator<ConfigEntry> iterator() {
        return child.iterator();
    }
}
