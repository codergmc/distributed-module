package com.gmc.config.file;

import com.gmc.config.ConfigDefiner;
import com.gmc.config.ConfigKey;
import com.gmc.config.ConfigProperty;
import com.gmc.config.ConfigPropertyImpl;
import com.gmc.config.ConfigValue;
import com.gmc.config.GlobalParams;
import com.gmc.config.process.OneConfigEntryProcessContext;
import com.gmc.config.process.Processors;
import org.yaml.snakeyaml.Yaml;

import java.util.Map;
import java.util.Optional;

public class YamlFileResolver implements FileResolver<FileResource> {
    public static YamlFileResolver INSTANCE = new YamlFileResolver();

    @Override
    public boolean support(String fileName) {
        return false;
    }

    @Override
    public ConfigProperty resolve(FileResource fileResource) throws Exception {
        Yaml yaml = new Yaml();
        Iterable<Object> objects = yaml.loadAll(fileResource.getInputStream());
        ConfigProperty configProperty = new ConfigPropertyImpl();
        for (Object object : objects) {
            parse(object, null, configProperty);
        }
        return configProperty;
    }

    private void parse(Object object, String keyPrefix, ConfigProperty configProperty) {
        ConfigDefiner configDefiner = GlobalParams.getConfigDefiner();
        Processors processors = GlobalParams.getProcessors();
        if (keyPrefix == null || configDefiner.containPath(keyPrefix)) {
            if (object instanceof Map) {
                ((Map<?, ?>) object).forEach((k, v) -> {
                    if (k instanceof String) {
                        String newKeyPrefix = keyPrefix == null ? (String) k : keyPrefix + "." + k;
                        parse(v, newKeyPrefix, configProperty);
                    }
                });
            }
        } else {
            ConfigKey configKey = configDefiner.getConfigKey(keyPrefix);
            Optional.ofNullable(configKey).ifPresent(key -> {

                ConfigValue configValue = key.newConfigValue();
                Object value = processors.preprocessOneConfigEntry(OneConfigEntryProcessContext.create(configKey, configValue, object, configProperty));
                configValue.mergeMidValue(keyPrefix, value);
                /**
                 * direct invoke rather than invoke in the end of {@link com.gmc.config.file.YamlFileResolver#resolve},because of yaml can represent complex types
                 */
                configValue.generateValue();
                processors.postProcessOneConfigEntry(OneConfigEntryProcessContext.create(configKey, configValue, null, configProperty));
                configProperty.put(key, configValue);

            });
        }


    }

    public static void main(String[] args) {


    }
}
