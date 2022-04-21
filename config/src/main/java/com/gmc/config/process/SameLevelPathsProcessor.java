package com.gmc.config.process;

import com.gmc.config.ConfigDefiner;
import com.gmc.config.ConfigProperties;
import com.gmc.config.ConfigProperty;
import com.gmc.config.Resource;

import java.util.List;

public interface SameLevelPathsProcessor extends Processor {
    void processSameLevelPaths(ConfigProperty configProperty);


}
