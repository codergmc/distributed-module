package com.gmc.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

public abstract class ChangeNotifyParameterResource<T extends ChangeNotifyResource<T>> extends ParameterResource implements ChangeNotifyResource<T> {
    protected List<ResourceChangeListener> changeListeners = new ArrayList<>();

    public ChangeNotifyParameterResource(Map<String, List<String>> params) {
        super(params);
    }

    @Override
    public void addChangeNotifyListener(ResourceChangeListener resourceChangeListener) {
        changeListeners.add(resourceChangeListener);
    }
}
