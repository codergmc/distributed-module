package com.gmc.config.process;

import com.gmc.config.ConfigValue;
import com.gmc.config.GlobalParams;

import java.util.function.Supplier;

public abstract class LazySupportOneConfigEntryPostProcessor implements OneConfigEntryPostProcessor {
    @Override
    public void postprocessOneConfigEntry(OneConfigEntryProcessContext oneConfigEntryProcessContext) {
        if (oneConfigEntryProcessContext.getConfigValue().isManualGenerate()) {
            GlobalParams copy = GlobalParams.deepCopy();
            GlobalParams instance = GlobalParams.INSTANCE;
            oneConfigEntryProcessContext.getConfigValue().addManualGenerateValueListener(() -> {
                GlobalParams.INSTANCE = copy;
                postprocessOneConfigEntry0(oneConfigEntryProcessContext);
                GlobalParams.INSTANCE = instance;

            });

        } else {
            postprocessOneConfigEntry0(oneConfigEntryProcessContext);
        }

    }


    protected abstract void postprocessOneConfigEntry0(OneConfigEntryProcessContext oneConfigEntryProcessContext);
}
