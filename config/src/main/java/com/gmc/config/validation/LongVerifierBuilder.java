package com.gmc.config.validation;

import com.gmc.core.LogUtils;

public class LongVerifierBuilder {
    private Verifiers<Long> verifiers = new Verifiers<>();

    public static LongVerifierBuilder builder() {
        return new LongVerifierBuilder();
    }

    public Verifiers<Long> build() {
        return verifiers;
    }

    public LongVerifierBuilder between(long min, long max, boolean minInclude, boolean maxInclude) {
        verifiers.add(new Between(min, max, minInclude, maxInclude));
        return this;
    }

    class Between implements ReportVerifier<Long> {
        long min;
        long max;
        boolean minInclude;
        boolean maxInclude;

        public Between(long min, long max, boolean minInclude, boolean maxInclude) {
            this.min = min;
            this.max = max;
            this.minInclude = minInclude;
            this.maxInclude = maxInclude;
        }

        @Override
        public boolean validate(Long v) {
            if (v < min || (min == v && !minInclude)) {
                return false;
            }
            if (v > max || (max == v && !maxInclude)) {
                return false;
            }
            return true;
        }

        @Override
        public void report() throws Exception {
            throw new IllegalArgumentException(LogUtils.format("the value must between {} and {}", min, max));
        }
    }
}
