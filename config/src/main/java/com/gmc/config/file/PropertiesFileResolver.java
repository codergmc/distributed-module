package com.gmc.config.file;

import com.gmc.config.ChangeNotifyResource;
import com.gmc.config.ConfigDefiner;
import com.gmc.config.ConfigKey;
import com.gmc.config.ConfigProperty;
import com.gmc.config.ConfigPropertyImpl;
import com.gmc.config.ConfigValue;
import com.gmc.config.GlobalParams;
import com.gmc.config.process.OneConfigEntryProcessContext;
import com.gmc.config.process.Processors;
import com.gmc.core.LogUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesFileResolver implements FileResolver<FileResource> {
    static final Logger LOGGER = LoggerFactory.getLogger(PropertiesFileResolver.class);
    public static final PropertiesFileResolver INSTANCE = new PropertiesFileResolver();

    @Override
    public ConfigProperty resolve(FileResource resource) throws Exception {
        ConfigPropertyImpl configProperty = new ConfigPropertyImpl();
        resolve0(resource, configProperty);
        if (resource instanceof ChangeNotifyResource) {
            ((ChangeNotifyResource) resource).addChangeNotifyListener(context1 -> {
                ConfigPropertyImpl newConfigProperty = new ConfigPropertyImpl();
                resolve0((FileResource) context1.getResource(), newConfigProperty);
                configProperty.mergeNewConfigProperty(newConfigProperty);

            });
        }
        return configProperty;
    }

    public void resolve0(FileResource resource, ConfigPropertyImpl configProperty) throws IOException {
        ConfigDefiner configDefiner = GlobalParams.getConfigDefiner();
        Processors processors = GlobalParams.getProcessors();
        InputStream inputStream = resource.getInputStream();
        Properties properties = new Properties();
        properties.load(inputStream);
        properties.forEach((k, v) -> {
            ConfigKey configKey = configDefiner.getConfigKey((String) k);
            ConfigValue configValue = configProperty.getConfigValue(configKey);
            if (configKey == null) {
                ConfigKey<?> matchConfigKey = configDefiner.findMatchConfigKey((String) k);
                if (matchConfigKey == null) {
                    LOGGER.warn(LogUtils.format("un support config key:{}", k));
                    return;
                } else {
                    configKey = matchConfigKey;
                }
            }

            if (configValue == null) {
                configValue = configKey.newConfigValue();
                configProperty.put(configKey, configValue);

            }
            v = processors.preprocessOneConfigEntry(OneConfigEntryProcessContext.create(configKey, configValue, v, configProperty));
            configValue.mergeMidValue((String) k, v);

        });
        //lazy invoke,because of properties can not represent complex types.only by parsing multiple configuration items can you get complex types
        configProperty.iterator().forEachRemaining(configEntry -> {
            configEntry.getConfigValue().generateValue();
            processors.postProcessOneConfigEntry(OneConfigEntryProcessContext.create(configEntry.getConfigKey(), configEntry.getConfigValue(), null, configProperty));
        });
        processors.processOneResource(configProperty);
    }


    @Override
    public boolean support(String fileName) {
        return false;
    }

}
