package com.gmc.config.file;

import com.gmc.config.ConfigProperty;
import com.gmc.config.Resource;
import com.gmc.config.ResourceResolver;

public class FileResourceResolver implements ResourceResolver {
    public static final FileResourceResolver INSTANCE = new FileResourceResolver();

    @Override
    public boolean support(Resource resource) {
        return resource instanceof FileResource;
    }

    @Override
    public ConfigProperty resolve(Resource resource) throws Exception {
        FileResource fileResource = (FileResource) resource;
        ConfigProperty configProperty;
        String name = fileResource.getFile().getName().toLowerCase();
        if (name.endsWith(".xml")) {
            configProperty = XmlFileResolver.INSTANCE.resolve(fileResource);
        } else if (name.endsWith(".properties")) {
            configProperty = PropertiesFileResolver.INSTANCE.resolve(fileResource);

        } else if (name.endsWith(".yml")) {
            configProperty = YamlFileResolver.INSTANCE.resolve(fileResource);
        } else {
            throw new IllegalArgumentException();
        }
        return configProperty;
    }
}
