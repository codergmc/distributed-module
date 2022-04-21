package com.gmc.core;

public class Tuple3<A,B,C>{
    private A v1;
    private B v2;
    private C v3;

    public Tuple3(A v1, B v2, C v3) {
        this.v1 = v1;
        this.v2 = v2;
        this.v3 = v3;
    }
    public static <A,B,C> Tuple3<A,B,C> of(A v1,B v2,C v3){
        return new Tuple3<>(v1,v2,v3);
    }
    public Tuple3() {
    }

    public A getV1() {
        return v1;
    }


    public B getV2() {
        return v2;
    }

    public C getV3() {
        return v3;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Tuple3<?, ?, ?> tuple3 = (Tuple3<?, ?, ?>) o;

        if (v1 != null ? !v1.equals(tuple3.v1) : tuple3.v1 != null) return false;
        if (v2 != null ? !v2.equals(tuple3.v2) : tuple3.v2 != null) return false;
        return v3 != null ? v3.equals(tuple3.v3) : tuple3.v3 == null;
    }

    @Override
    public int hashCode() {
        int result = v1 != null ? v1.hashCode() : 0;
        result = 31 * result + (v2 != null ? v2.hashCode() : 0);
        result = 31 * result + (v3 != null ? v3.hashCode() : 0);
        return result;
    }
}
