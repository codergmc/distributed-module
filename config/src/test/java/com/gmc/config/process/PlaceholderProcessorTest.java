package com.gmc.config.process;

import com.gmc.config.Config;
import com.gmc.config.ConfigDefiner;
import com.gmc.config.ConfigProperty;
import com.gmc.config.ConfigPropertyImpl;
import com.gmc.config.ConfigPropertyUtils;
import com.gmc.config.ConfigValue;
import com.gmc.config.GlobalParams;
import com.gmc.config.convert.ConvertSupportResultFactory;
import com.gmc.config.convert.TypeConverters;
import com.gmc.config.file.Configs;
import com.gmc.config.file.FileResource;
import com.gmc.config.file.YamlFileResolver;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PlaceholderProcessorTest {
    @Test
    void test() throws Exception {
        String paths = "file://./src/test/java/com/gmc/config/process/test.yml";
        Config config = new Config.ConfigBuilder().configClass(Configs.class).paths(paths).processor(new PlaceholderProcessor()).build();
        ConfigProperty property = config.getConfig();
        ConfigPropertyUtils.validation(paths,property);

    }

}