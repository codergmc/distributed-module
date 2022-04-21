package com.gmc.config.validation;

public class EnumVerifierBuilder {
    private Verifiers<String> verifiers = new Verifiers<>();

    public static EnumVerifierBuilder builder() {
        return new EnumVerifierBuilder();
    }

    public EnumVerifierBuilder of(Class<? extends Enum> enumType) {
        verifiers.add(t -> {
            try {
                Enum.valueOf(enumType, t);
                return true;
            } catch (Exception e) {
                //todo
                throw new IllegalArgumentException("");
            }


        });
        return this;
    }

    public Verifiers<String> build() {
        return verifiers;
    }

}
