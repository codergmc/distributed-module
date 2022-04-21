package com.gmc.config;

import com.gmc.config.file.FileResourceResolver;

import java.util.ArrayList;
import java.util.List;

public class ResourceResolvers {
    List<ResourceResolver> resolvers = new ArrayList<>();

    public ResourceResolvers(List<ResourceResolver> resolvers) {
        assert resolvers!=null;
        this.resolvers.addAll(resolvers);
        this.resolvers.add(new FileResourceResolver());

    }

    public ConfigProperty resolve(List<Resource> resources) {
        ConfigProperties configProperties = new ConfigProperties();
        for (Resource resource : resources) {
            ConfigProperty configProperty = resolvers.stream().filter(resolver -> resolver.support(resource))
                    .findFirst()
                    .map(resolver -> {
                        try {
                            GlobalParams.INSTANCE.addParam(Config.CUR_RESOURCE, resources);
                            ConfigProperty curConfigProperty = resolver.resolve(resource);
                            GlobalParams.getProcessors().processOneResource(curConfigProperty);
                            return curConfigProperty;
                        } catch (Exception e) {
                            throw new IllegalArgumentException();
                        }
                    })
                    .orElse(null);
            if (configProperty != null) {
                configProperties.add(configProperty);
            }
        }
        GlobalParams.getProcessors().processOnePath(configProperties);
        return configProperties;
    }
}
