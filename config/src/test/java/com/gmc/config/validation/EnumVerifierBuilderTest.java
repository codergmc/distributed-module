package com.gmc.config.validation;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EnumVerifierBuilderTest {
    @Test
    void testEnum() throws Exception {
        assertTrue(EnumVerifierBuilder.builder().of(TestEnum.class).build().validate("A"));
        assertThrows(IllegalArgumentException.class, () -> EnumVerifierBuilder.builder().of(TestEnum.class).build().validate("a"));
        assertThrows(IllegalArgumentException.class, () -> EnumVerifierBuilder.builder().of(TestEnum.class).build().validate("d"));


    }

    enum TestEnum {
        A, B, C;
    }

}