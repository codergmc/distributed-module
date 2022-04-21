package com.gmc.config.file;

import com.gmc.config.ConfigDefiner;
import com.gmc.config.ConfigProperty;
import com.gmc.config.ConfigPropertyImpl;
import com.gmc.config.ConfigValue;
import com.gmc.config.GlobalParams;
import com.gmc.config.process.OneConfigEntryProcessContext;
import com.gmc.config.process.Processors;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.util.Optional;

public class XmlFileResolver implements FileResolver<FileResource> {
    public static final XmlFileResolver INSTANCE = new XmlFileResolver();

    @Override
    public ConfigProperty resolve(FileResource resource) throws Exception {
        ConfigProperty configProperty = new ConfigPropertyImpl();
        SAXReader saxReader = new SAXReader();
        Document document = saxReader.read(resource.getInputStream());
        Element rootElement = document.getRootElement();
        parse(rootElement, configProperty, null);
        return configProperty;

    }

    public void parse(Element element, ConfigProperty configProperty, String keyPrefix) {
        ConfigDefiner configDefiner = GlobalParams.getConfigDefiner();
        Processors processors = GlobalParams.getProcessors();
        element.attributeIterator().forEachRemaining(attribute -> {
            String name = attribute.getName();
            String key = (keyPrefix != null ? (keyPrefix + ".") : "") + name;
            String value = attribute.getValue();
            Optional.ofNullable(configDefiner.getConfigKey(key)).ifPresent(configKey -> {
                ConfigValue configValue = configKey.newConfigValue();
                Object value1 = processors.preprocessOneConfigEntry(OneConfigEntryProcessContext.create(configKey, configValue, value, configProperty));
                configValue.mergeMidValue(key, value1);
                configValue.generateValue();
                configProperty.put(configKey, configValue);
            });

        });
        element.elementIterator().forEachRemaining(child ->
                parse(child, configProperty, keyPrefix + "." + element.getName())
        );


    }

    @Override
    public boolean support(String fileName) {
        return false;
    }


}
