package com.gmc.config.validation;

public interface Verifier<T> {
    public boolean validate(T t) throws Exception;

}
