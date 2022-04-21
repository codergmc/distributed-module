package com.gmc.config.process;

import com.gmc.config.ConfigProperty;

public interface OneResourceProcessor extends Processor {
    public void processResource(ConfigProperty configProperty);


}
