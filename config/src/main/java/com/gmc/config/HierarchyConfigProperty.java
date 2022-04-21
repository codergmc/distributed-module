package com.gmc.config;

import java.util.Iterator;

public class HierarchyConfigProperty implements ConfigProperty {
    protected HierarchyConfigProperty child;
    protected HierarchyConfigProperty parent;
    protected ConfigProperty configProperty;

    public HierarchyConfigProperty(ConfigProperty configProperty) {
        this.configProperty = configProperty;
    }

    public ConfigProperty getParent() {
        return parent;
    }

    public HierarchyConfigProperty getChild() {
        return child;
    }

    public HierarchyConfigProperty setChild(HierarchyConfigProperty child) {
        this.child = child;
        return this;
    }
    public HierarchyConfigProperty setParent(HierarchyConfigProperty parent) {
        this.parent = parent;
        return this;
    }
    public boolean isEmpty(){
        return configProperty==null;
    }

    public HierarchyConfigProperty setConfigProperty(ConfigProperty configProperty) {
        this.configProperty = configProperty;
        return this;
    }

    @Override
    public <T> void put(ConfigKey<T> key, ConfigValue<T> value) {
        throw new UnsupportedOperationException();
    }

    public <T> ConfigValue<T> getConfigValue(ConfigKey<T> configKey) {
        ConfigValue<T> configValue = getThisConfigValue(configKey);
        if (configValue == null && child != null) {
            configValue = child.getConfigValue(configKey);
        }
        return configValue;
    }

    @Override
    public void mergeNewConfigProperty(ConfigProperty configProperty) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <V> boolean addConfigListener(ConfigKey<V> configKey, ConfigListener<V> configListener) {
        if (configProperty != null && configProperty.contain(configKey)) {
            return configProperty.addConfigListener(configKey, configListener);
        } else if (child != null)
            return child.addConfigListener(configKey, configListener);
        return false;
    }

    protected <V> boolean thisContain(ConfigKey<V> configKey) {
        if (configProperty != null) {
            return configProperty.contain(configKey);
        }
        return false;
    }

    @Override
    public <V> boolean contain(ConfigKey<V> configKey) {
        boolean b = thisContain(configKey);
        if (!b && child != null) {
            return child.contain(configKey);
        }
        return false;
    }

    public static HierarchyConfigProperty wrap(ConfigProperty configProperty) {
        if (configProperty instanceof HierarchyConfigProperty) {
            return (HierarchyConfigProperty) configProperty;
        }
        return new HierarchyConfigProperty(configProperty);
    }

    @Override
    public ConfigProperty get(int index) {
        assert index >= 0;
        if (index == 0) {
            return this;
        }
        if (child == null) {
            throw new IllegalArgumentException();
        }
        return child.get(index - 1);

    }
    public void addInner(int index, ConfigProperty configProperty){
        this.configProperty.add(index,configProperty);
    }
    public void addInner(ConfigProperty configProperty){
        configProperty.add(configProperty);
    }

    @Override
    public void add(int index, ConfigProperty configProperty) {
        assert index >= 0;
        assert configProperty instanceof HierarchyConfigProperty;
        HierarchyConfigProperty hierarchyConfigProperty = (HierarchyConfigProperty) configProperty;
        if (index == 0) {
            hierarchyConfigProperty.setParent(parent);
            parent.setChild(hierarchyConfigProperty);
            return;
        }
        if (child == null) {
            throw new IllegalArgumentException();
        }
        child.add(index - 1, configProperty);

    }

    @Override
    public void add(ConfigProperty configProperty) {
        assert configProperty instanceof HierarchyConfigProperty;
        if (child != null) {
            child.add(configProperty);
        } else {
            HierarchyConfigProperty hierarchyConfigProperty = (HierarchyConfigProperty) configProperty;
            this.setChild(hierarchyConfigProperty);
            hierarchyConfigProperty.setParent(this);
        }
    }

    protected <T> ConfigValue<T> getThisConfigValue(ConfigKey<T> configKey) {
        if(configProperty!=null){
            return configProperty.getConfigValue(configKey);
        } else return null;
    }


    @Override
    public Iterator<ConfigEntry> iterator() {
        if(configProperty==null){
            return new Iterator<ConfigEntry>() {
                @Override
                public boolean hasNext() {
                    return false;
                }

                @Override
                public ConfigEntry next() {
                    return null;
                }
            };
        }
        Iterator<ConfigEntry> iterator = configProperty.iterator();
        return new Iterator<>() {
            Iterator<ConfigEntry> currentIterator = iterator;
            ConfigEntry next;
            HierarchyConfigProperty currentConfigProperty = HierarchyConfigProperty.this;

            @Override
            public boolean hasNext() {
                if (currentIterator != null) {
                    boolean hasNext = currentIterator.hasNext();
                    if (hasNext) {
                        next = currentIterator.next();
                        return true;
                    } else {
                        currentIterator = null;
                        return hasNext();
                    }

                } else {
                    HierarchyConfigProperty child = currentConfigProperty.getChild();
                    if (child == null) {
                        return false;
                    } else {
                        currentIterator = child.iterator();
                        return hasNext();
                    }
                }
            }

            @Override
            public ConfigEntry next() {
                return next;
            }
        };
    }
}
