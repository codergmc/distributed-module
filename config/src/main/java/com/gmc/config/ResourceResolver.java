package com.gmc.config;


public interface ResourceResolver {
    boolean support(Resource resources);

    public ConfigProperty resolve(Resource resources) throws Exception;

}
