package com.gmc.config.validation;

import java.util.ArrayList;
import java.util.List;

public class Verifiers<T> implements Verifier<T> {
    private List<Verifier<T>> verifiers = new ArrayList<>();
    public void add(Verifier<T> verifier){
        verifiers.add(verifier);
    }
    @Override
    public boolean validate(T t) throws Exception {
        for (Verifier<T> verifier : verifiers) {
            if (!verifier.validate(t)) {
                if (verifier instanceof ReportVerifier) {
                    ((ReportVerifier<T>) verifier).report();
                    return false;
                }
            }
        }
        return true;
    }

}
