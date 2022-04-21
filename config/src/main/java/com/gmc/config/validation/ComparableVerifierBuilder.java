package com.gmc.config.validation;

import com.gmc.core.LogUtils;


public class ComparableVerifierBuilder<T extends Comparable<T>> {
    private final Verifiers<T> verifiers = new Verifiers<>();

    public static <T extends Number & Comparable<T>> ComparableVerifierBuilder<T> builder(Class<T> tClass) {
        return new ComparableVerifierBuilder<>();
    }

    public ComparableVerifierBuilder<T> greaterThan(T t, boolean include) {
        verifiers.add(new Greater<>(t, include));
        return this;
    }


    public ComparableVerifierBuilder<T> between(T min, T max, boolean minInclude, boolean maxInclude) {
        verifiers.add(new Between<>(min, max, minInclude, maxInclude));
        return this;
    }

    public Verifiers<T> build() {
        return verifiers;
    }

    static class Greater<R extends Comparable<R>> implements ReportVerifier<R> {
        private final R min;
        private final boolean include;

        public Greater(R min, boolean include) {
            this.min = min;
            this.include = include;
        }

        @Override
        public void report() {
            throw new IllegalArgumentException(LogUtils.format("the value must greater than {} ", min));
        }

        @Override
        public boolean validate(R r) {
            int i = r.compareTo(min);
            return (i == 0 && include) || i > 0;
        }
    }

    static class Less<R extends Comparable<R>> implements ReportVerifier<R> {
        private final R max;
        private final boolean include;

        public Less(R max, boolean include) {
            this.max = max;
            this.include = include;
        }

        @Override
        public void report() {
            throw new IllegalArgumentException(LogUtils.format("the value must greater than {} ", max));
        }

        @Override
        public boolean validate(R r) {
            int i = r.compareTo(max);
            return (i == 0 && include) || i < 0;
        }
    }

    static class Between<R extends Comparable<R>> implements ReportVerifier<R> {
        private final R min;
        private final R max;
        private final boolean minInclude;
        private final boolean maxInclude;

        public Between(R min, R max, boolean minInclude, boolean maxInclude) {
            this.min = min;
            this.max = max;
            this.minInclude = minInclude;
            this.maxInclude = maxInclude;
        }

        @Override
        public void report() {
            throw new IllegalArgumentException(LogUtils.format("the value must between {} and {}", min, max));
        }

        @Override
        public boolean validate(R r) {
            int i = r.compareTo(min);
            if ((i == 0 && !minInclude) || i < 0)
                return false;
            i = r.compareTo(max);
            if ((i == 0 && !maxInclude) || i > 0)
                return false;
            return true;
        }
    }

}
