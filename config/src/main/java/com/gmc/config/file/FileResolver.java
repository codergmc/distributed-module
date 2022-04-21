package com.gmc.config.file;

import com.gmc.config.ConfigProperty;

public interface FileResolver<T extends FileResource> {
    boolean support(String fileName);
    ConfigProperty resolve(T resource) throws Exception;
}
