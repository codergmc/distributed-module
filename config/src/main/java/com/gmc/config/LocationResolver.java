package com.gmc.config;

import com.gmc.config.process.SameLevelPathsProcessor;

import java.util.List;

public interface LocationResolver<R extends Resource> {
    public boolean support(Location location);

    public List<R> resolve(Location location);

}
