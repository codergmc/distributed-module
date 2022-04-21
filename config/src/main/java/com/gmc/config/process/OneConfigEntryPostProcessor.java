package com.gmc.config.process;

public interface OneConfigEntryPostProcessor extends Processor {
    void postprocessOneConfigEntry(OneConfigEntryProcessContext oneConfigEntryProcessContext);

}
