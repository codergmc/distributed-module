package com.gmc.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class ParameterLocation implements Location {
    protected Map<String, List<String>> params = new HashMap<>();

    public ParameterLocation(Map<String, List<String>> params) {
        this.params = params;
    }

    public List<String> getParam(String key) {
        return params.get(key);
    }

    public Map<String, List<String>> getParams() {
        return params;
    }
}
