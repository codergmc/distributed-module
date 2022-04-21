package com.gmc.config.file;

import com.gmc.config.ConfigKey;
import com.gmc.config.TypeReferenceImpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Configs {
    public static final ConfigKey<String> STRING = ConfigKey.ConfigKeyBuilder.builder("string", new TypeReferenceImpl<String>() {
    }).defaultValue("default").build();
    public static final ConfigKey<String> STRING_CONFIG_KEY = ConfigKey.ConfigKeyBuilder.builder("a.string", new TypeReferenceImpl<String>() {
    }).defaultValue("default").build();
    public static final ConfigKey<Integer> INTEGER_CONFIG_KEY = ConfigKey.ConfigKeyBuilder.builder("a.integer", new TypeReferenceImpl<Integer>() {
    }).defaultValue(1).build();
    public static final ConfigKey<Double> DOUBLE_CONFIG_KEY = ConfigKey.ConfigKeyBuilder.builder("a.double", new TypeReferenceImpl<Double>() {
    }).defaultValue(1.1d).build();
    public static final ConfigKey<List<Integer>> LIST_INTEGER_CONFIG_KEY = ConfigKey.ConfigKeyBuilder.builder("a.list.integer", new TypeReferenceImpl<List<Integer>>() {
    }).defaultValue(Arrays.asList(1, 1, 1)).build();
}
