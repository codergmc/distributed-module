package com.gmc.config.process;

import com.gmc.config.ConfigKey;
import com.gmc.config.ConfigProperty;
import com.gmc.config.ConfigValue;
import com.gmc.config.Resource;

public class ProfileProcess implements OneConfigEntryPostProcessor,OneResourceProcessor {
    private ConfigKey activeProfileConfigKey;
    private ConfigValue activeProfileConfigValue;

    @Override
    public void postprocessOneConfigEntry(OneConfigEntryProcessContext oneConfigEntryProcessContext) {
        if(activeProfileConfigValue==null) {
            if (oneConfigEntryProcessContext.getConfigKey().equals(activeProfileConfigKey)) {
                this.activeProfileConfigValue = oneConfigEntryProcessContext.getConfigValue();
            }
        }
    }

    @Override
    public void processResource(ConfigProperty configProperty) {

    }
}
