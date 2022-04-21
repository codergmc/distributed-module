package com.gmc.config.file;

import com.gmc.config.Config;
import com.gmc.config.ConfigDefiner;
import com.gmc.config.ConfigProperty;
import com.gmc.config.GlobalParams;
import com.gmc.config.process.Processors;
import com.gmc.config.convert.ConvertSupportResultFactory;
import com.gmc.config.convert.TypeConverters;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.ObjectInputFilter;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PropertiesFileResolverTest {
    @Test
    void test() throws Exception {
        PropertiesFileResolver fileResolver = new PropertiesFileResolver();
        File file = new File("./src/test/java/com/gmc/config/file/test.properties");
        FileResource fileResource = new FileResource(file, Collections.emptyMap());
        Processors preprocessors = Processors.create();
        TypeConverters typeConverters = new TypeConverters(Collections.emptyList(), new ConvertSupportResultFactory());
        ConfigDefiner configDefiner = new ConfigDefiner(typeConverters, Configs.class);
        GlobalParams.INSTANCE.addParam(Config.PROCESSORS,preprocessors);
        GlobalParams.INSTANCE.addParam(Config.TYPE_CONVERTERS,typeConverters);
        GlobalParams.INSTANCE.addParam(Config.CONFIG_DEFINER,configDefiner);
        ConfigProperty property = fileResolver.resolve(fileResource);
        assertTrue(property.getConfigValue(Configs.DOUBLE_CONFIG_KEY).getValue() == 4.4d);
        assertTrue(property.getConfigValue(Configs.INTEGER_CONFIG_KEY).getValue() == 4);
        assertTrue(property.getConfigValue(Configs.STRING_CONFIG_KEY).getValue().equals("string"));
        List<Integer> integerList = property.getConfigValue(Configs.LIST_INTEGER_CONFIG_KEY).getValue();
        assertTrue(integerList.size() == 3);
        assertTrue(integerList.get(0) == 1);
        assertTrue(integerList.get(1) == 2);
        assertTrue(integerList.get(2) == 3);

    }

}