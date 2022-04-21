package com.gmc.core;

public class Tuple2<K, V> {
    private K v1;
    private V v2;

    public Tuple2(K k, V v) {
        this.v1 = k;
        this.v2 = v;
    }

    public static <K, V> Tuple2<K, V> of(K k, V v) {
        return new Tuple2<>(k, v);

    }

    public Tuple2() {
    }

    public K getV1() {
        return v1;
    }


    public V getV2() {
        return v2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Tuple2<?, ?> tuple2 = (Tuple2<?, ?>) o;

        if (v1 != null ? !v1.equals(tuple2.v1) : tuple2.v1 != null) return false;
        return v2 != null ? v2.equals(tuple2.v2) : tuple2.v2 == null;
    }

    @Override
    public int hashCode() {
        int result = v1 != null ? v1.hashCode() : 0;
        result = 31 * result + (v2 != null ? v2.hashCode() : 0);
        return result;
    }
}
