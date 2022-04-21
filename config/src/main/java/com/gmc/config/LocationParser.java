package com.gmc.config;

public interface LocationParser<T extends Location> {
    public boolean support(String path);
    public T parse(String path);
}
