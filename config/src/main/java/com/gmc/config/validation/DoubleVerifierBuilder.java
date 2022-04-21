package com.gmc.config.validation;

import com.gmc.core.LogUtils;

public class DoubleVerifierBuilder {
    private Verifiers<Double> verifiers = new Verifiers<>();

    public static DoubleVerifierBuilder builder() {
        return new DoubleVerifierBuilder();
    }

    public Verifiers<Double> build() {
        return verifiers;
    }

    public DoubleVerifierBuilder between(double min, double max, boolean minInclude, boolean maxInclude) {
        verifiers.add(new DoubleVerifierBuilder.Between(min, max, minInclude, maxInclude));
        return this;
    }

    class Between implements ReportVerifier<Double> {
        double min;
        double max;
        boolean minInclude;
        boolean maxInclude;

        public Between(double min, double max, boolean minInclude, boolean maxInclude) {
            this.min = min;
            this.max = max;
            this.minInclude = minInclude;
            this.maxInclude = maxInclude;
        }

        @Override
        public boolean validate(Double d) {
            if (d < min || (min == d && !minInclude)) {
                return false;
            }
            if (d > max || (max == d && !maxInclude)) {
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
