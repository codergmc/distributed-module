package com.gmc.config;

import com.gmc.config.convert.TypeConverter;
import com.gmc.config.convert.TypeConverters;
import com.gmc.config.process.Processors;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.locks.ReadWriteLock;

public class GlobalParams {
    public static GlobalParams INSTANCE = new GlobalParams();
    private Map<String, Object> params = new ConcurrentHashMap<>();

    public <T> T addParam(String key, T object) {
        return (T) INSTANCE.params.put(key, object);
    }

    public <T> T getParam(String key) {
        return (T) INSTANCE.params.get(key);
    }

    public static void remove(String key) {
        INSTANCE.params.remove(key);

    }

    public static Paths.SameLevelPath getCurrentSameLevelPath() {
        return (Paths.SameLevelPath) INSTANCE.params.get(Config.CUR_SAME_LEVEL_PATH);
    }

    public static Paths.Path getCurrentPath() {
        return (Paths.Path) INSTANCE.params.get(Config.CUR_PATH);
    }

    public static ConfigProperty getHeadConfigProperty() {
        return (ConfigProperty) INSTANCE.params.get(Config.HEAD_CONFIG_PROPERTY);
    }

    public static TypeConverters getTypeConverters() {
        return (TypeConverters) INSTANCE.params.get(Config.TYPE_CONVERTERS);
    }

    public static ScheduledExecutorService getExecutors() {
        return (ScheduledExecutorService) INSTANCE.params.get(Config.EXECUTORS);
    }

    public static ConfigDefiner getConfigDefiner() {
        return (ConfigDefiner) INSTANCE.params.get(Config.CONFIG_DEFINER);
    }

    public static Processors getProcessors() {
        return (Processors) INSTANCE.params.get(Config.PROCESSORS);
    }

    public static List<Resource> getCurrentResources() {
        return (List<Resource>) INSTANCE.params.get(Config.CUR_RESOURCES);
    }

    public static Resource getCurrentResource() {
        return (Resource) INSTANCE.params.get(Config.CUR_RESOURCE);
    }

    public static ConfigProperty getCurrentSameLevelConfigProperty() {
        return (ConfigProperty) INSTANCE.params.get(Config.CUR_SAME_LEVEL_HEAD_CONFIG_PROPERTY);
    }

    public static GlobalParams deepCopy() {
        GlobalParams globalParams = new GlobalParams() {
            @Override
            public <T> T addParam(String key, T object) {
                throw new UnsupportedOperationException();
            }
        };
        globalParams.params.putAll(INSTANCE.params);
        return globalParams;
    }

}
