package com.gmc.config.validation;

public interface ReportVerifier<T> extends Verifier<T>{
    public void report() throws Exception;
}
