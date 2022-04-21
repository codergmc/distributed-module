package com.gmc.config.process;

import com.gmc.config.ConfigDefiner;
import com.gmc.config.ConfigKey;
import com.gmc.config.ConfigProperty;
import com.gmc.config.ConfigPropertyImpl;
import com.gmc.config.GlobalParams;
import com.gmc.config.HierarchyConfigProperty;

public class DefaultValueProcessor implements AllPathsProcessor {
    @Override
    public void processAllPaths() {
        ConfigProperty headConfigProperty = GlobalParams.getHeadConfigProperty();
        ConfigDefiner configDefiner = GlobalParams.getConfigDefiner();
        ConfigProperty configProperty = new ConfigPropertyImpl();
        for (ConfigKey key : configDefiner.getConfigKeyList()) {
            configProperty.put(key, key.newDefaultConfigValue());
        }
        headConfigProperty.add(HierarchyConfigProperty.wrap(configProperty));

    }
}
