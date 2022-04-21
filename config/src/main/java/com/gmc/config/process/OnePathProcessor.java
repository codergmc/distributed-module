package com.gmc.config.process;

import com.gmc.config.ConfigProperties;
import com.gmc.config.ConfigProperty;
import com.gmc.config.Resource;

public interface OnePathProcessor extends Processor {
    void processOnePath(ConfigProperty configProperty);


}
