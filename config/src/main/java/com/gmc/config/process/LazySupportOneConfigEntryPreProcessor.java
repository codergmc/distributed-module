package com.gmc.config.process;

import com.gmc.config.GlobalParams;

import java.util.function.Supplier;

public abstract class LazySupportOneConfigEntryPreProcessor implements OneConfigEntryPreProcessor {
    @Override
    public Object preprocessOneConfigEntry(OneConfigEntryProcessContext oneConfigEntryProcessContext) {

        if (needLazy(oneConfigEntryProcessContext) || oneConfigEntryProcessContext.getValue() instanceof Supplier) {
            GlobalParams copy = GlobalParams.deepCopy();
            GlobalParams globalParams = GlobalParams.INSTANCE;
            return (Supplier<Object>) () -> {
                GlobalParams.INSTANCE = copy;
                Object result = preprocessOneConfigEntry0(oneConfigEntryProcessContext);
                GlobalParams.INSTANCE = globalParams;
                return result;
            };
        } else {
            return preprocessOneConfigEntry0(oneConfigEntryProcessContext);
        }
    }

    protected abstract boolean needLazy(OneConfigEntryProcessContext oneConfigEntryProcessContext);

    protected abstract Object preprocessOneConfigEntry0(OneConfigEntryProcessContext oneConfigEntryProcessContext);
}
