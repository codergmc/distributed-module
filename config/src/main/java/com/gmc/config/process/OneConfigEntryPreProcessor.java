package com.gmc.config.process;

public interface OneConfigEntryPreProcessor extends Processor {
    Object preprocessOneConfigEntry(OneConfigEntryProcessContext oneConfigEntryProcessContext);

}
