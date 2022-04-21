package com.gmc.config;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class String2ListPreprocessorTest {
    @Test
    void test() {
        List<String> l = new ArrayList<>();
        Collections.sort(l, (s1, s2) -> {
            if (s1.length() > s2.length()) {
                return -1;
            }
            if (s1.length() == s2.length()) {
                return s1.compareTo(s2);
            }
            return 1;
        });

    }

}