package com.gmc.config;

import java.util.List;
import java.util.Map;

public abstract class ParameterResource implements Resource{
    protected Map<String, List<String>> params;

    public ParameterResource(Map<String, List<String>> params) {
        assert params!=null;
        this.params = params;
    }

}
