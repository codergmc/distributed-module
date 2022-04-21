package com.gmc.config;

import com.gmc.config.process.LazySupportOneConfigEntryPreProcessor;
import com.gmc.config.process.OneConfigEntryPreProcessor;
import com.gmc.config.process.OneConfigEntryProcessContext;

public class String2ListPreprocessor extends LazySupportOneConfigEntryPreProcessor {

    @Override
    public Object preprocessOneConfigEntry0(OneConfigEntryProcessContext context) {
        Object value = context.getValue();
        if (value instanceof String) {
            String[] split = ((String) value).split(",");
            if (split.length == 1)
                return value;
            else return split;
        }
        return value;
    }

    @Override
    protected boolean needLazy(OneConfigEntryProcessContext oneConfigEntryProcessContext) {
        return false;
    }

}
