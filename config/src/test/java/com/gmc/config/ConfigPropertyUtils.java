package com.gmc.config;

import com.gmc.config.file.Configs;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ConfigPropertyUtils {
    public static void validation(String path, ConfigProperty configProperty){

    }
    public static void validation(ConfigProperty configProperty){
        assertTrue(configProperty.getConfigValue(Configs.DOUBLE_CONFIG_KEY).getValue() == 4.4d);
        assertTrue(configProperty.getConfigValue(Configs.STRING).getValue().equals("4"));
        assertTrue(configProperty.getConfigValue(Configs.INTEGER_CONFIG_KEY).getValue() == 4);
        assertTrue(configProperty.getConfigValue(Configs.STRING_CONFIG_KEY).getValue().equals("string"));
        List<Integer> integerList = configProperty.getConfigValue(Configs.LIST_INTEGER_CONFIG_KEY).getValue();
        assertTrue(integerList.size() == 3);
        assertTrue(integerList.get(0) == 1);
        assertTrue(integerList.get(1) == 2);
        assertTrue(integerList.get(2) == 3);
    }
}
