package com.gmc.config;

import com.gmc.config.convert.ConvertSupportResultFactory;
import com.gmc.config.convert.ConvertSupportResultFactoryInterface;
import com.gmc.config.convert.TypeConverter;
import com.gmc.config.convert.TypeConverters;
import com.gmc.config.process.Processor;
import com.gmc.config.process.Processors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Supplier;

public class Config {
    public static final String EXECUTORS = "executors";
    public static final String TYPE_CONVERTERS = "typeConverters";
    public static final String CONFIG_DEFINER = "configDefiner";
    public static final String PROCESSORS = "processors";
    public static final String PATHS = "paths";
    public static final String HEAD_CONFIG_PROPERTY = "headConfigProperty";
    public static final String CUR_PATH = "currentPath";
    public static final String CUR_LOCATION = "currentLocation";
    public static final String CUR_RESOURCES = "currentResources";
    public static final String CUR_RESOURCE = "currentResource";
    public static final String CUR_SAME_LEVEL_PATH = "currentSameLevelPath";
    public static final String CUR_SAME_LEVEL_HEAD_CONFIG_PROPERTY = "currentSameLevelHeadConfigProperty";

    private final List<Class<?>> configClasses;
    private final List<LocationParser<? extends Location>> udLocationParsers;
    private final List<LocationResolver<? extends Resource>> udLocationResolvers;
    private final List<List<String>> paths;
    private final List<ResourceResolver> udResourceResolver;
    private final List<TypeConverter> udTypeConverter;
    private final ConvertSupportResultFactoryInterface convertSupportResultFactory = new ConvertSupportResultFactory();
    private final Map<Class<?>, Supplier<?>> udInstanceFactory;
    private final List<Processor> udPreprocessors;
    private ConfigDefiner configDefiner;
    private LocationParsers locationParsers;
    private LocationResolvers locationResolver;
    private Processors processors;
    private TypeConverters typeConverters;
    private ScheduledExecutorService executorService;
    private ResourceResolvers resourceResolver;
    private ConfigProperty head;

    private Config(List<Class<?>> configClasses, List<LocationParser<? extends Location>> udLocationParsers, List<LocationResolver<? extends Resource>> udLocationResolvers, List<List<String>> paths, List<ResourceResolver> udResourceResolver, List<TypeConverter> udTypeConverter, Map<Class<?>, Supplier<?>> udInstanceFactory, List<Processor> udPreprocessors) {
        this.configClasses = configClasses;
        this.udLocationParsers = udLocationParsers;
        this.udLocationResolvers = udLocationResolvers;
        this.paths = paths;
        this.udResourceResolver = udResourceResolver;
        this.udTypeConverter = udTypeConverter;
        this.udInstanceFactory = udInstanceFactory;
        this.udPreprocessors = udPreprocessors;
    }


    public ConfigProperty getConfig() throws Exception {
        assert udInstanceFactory != null;
        udInstanceFactory.forEach(InstanceFactory::registor);
        typeConverters = new TypeConverters(udTypeConverter, convertSupportResultFactory);
        processors = new Processors(udPreprocessors);
        assert configClasses != null;
        configClasses.add(InnerConfigDefiner.class);
        configDefiner = new ConfigDefiner(typeConverters, configClasses);
        locationParsers = new LocationParsers(udLocationParsers);
        locationResolver = new LocationResolvers(udLocationResolvers);
        executorService = Executors.newScheduledThreadPool(1);
        resourceResolver = new ResourceResolvers(udResourceResolver);
        Paths paths = new Paths(this.paths);
        head = new ConfigPropertyHead();
        GlobalParams.INSTANCE.addParam(HEAD_CONFIG_PROPERTY, head);
        GlobalParams.INSTANCE.addParam(EXECUTORS, executorService);
        GlobalParams.INSTANCE.addParam(TYPE_CONVERTERS, typeConverters);
        GlobalParams.INSTANCE.addParam(CONFIG_DEFINER, configDefiner);
        GlobalParams.INSTANCE.addParam(PROCESSORS, processors);
        GlobalParams.INSTANCE.addParam(PATHS, paths);
        processPaths(paths, head);
        return head;
    }

    public static ConfigBuilder builder() {
        return new ConfigBuilder();
    }

    private void processPaths(Paths paths, ConfigProperty head) {
        paths.pathProcess(path -> {
            Location location = locationParsers.parse(path);
            GlobalParams.INSTANCE.addParam(CUR_LOCATION, location);
            if (location != null) {
                List<Resource> resources = locationResolver.resolve(location);
                GlobalParams.INSTANCE.addParam(CUR_RESOURCES, resources);
                return resourceResolver.resolve(resources);
            } else {
                return null;
            }
        }, head);
        processors.processAllPaths();


    }


    public static class ConfigBuilder {
        List<Class<?>> configClasses = new ArrayList<>();
        List<Processor> processors = new ArrayList<>();
        List<List<String>> paths = new ArrayList<>();
        List<LocationParser<? extends Location>> locationParsers = new ArrayList<>();
        List<LocationResolver<? extends Resource>> locationResolvers = new ArrayList<>();
        List<ResourceResolver> resourceResolvers = new ArrayList<>();
        List<TypeConverter> typeConverters = new ArrayList<>();
        Map<Class<?>, Supplier<?>> instanceFactory = new HashMap<>();

        public ConfigBuilder configClass(Class<?>... classes) {
            configClasses.addAll(Arrays.asList(classes));
            return this;
        }

        public ConfigBuilder processor(Processor... processor) {
            processors.addAll(Arrays.asList(processor));
            return this;
        }

        public ConfigBuilder compactPaths(List<String> paths) {
            for (String path : paths) {
                String[] split = path.split(";");
                this.paths.add(Arrays.asList(split));
            }
            return this;
        }

        public ConfigBuilder compactPath(String path) {
            String[] split = path.split(";");
            this.paths.add(Arrays.asList(split));
            return this;
        }

        public ConfigBuilder paths(List<List<String>> paths) {
            this.paths.addAll(paths);
            return this;

        }

        public ConfigBuilder paths(String... paths) {
            this.paths.add(Arrays.asList(paths));
            return this;

        }

        public Config build() {
            return new Config(configClasses, locationParsers, locationResolvers, paths, resourceResolvers, typeConverters, instanceFactory, processors);
        }


    }


}
