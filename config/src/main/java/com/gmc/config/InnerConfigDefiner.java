package com.gmc.config;

public class InnerConfigDefiner {
    public static final ConfigKey<Long> LOCAL_FILE_LISTEN_CYCLE = ConfigKey.ConfigKeyBuilder.builder("localfile.listen.cycle", new TypeReferenceImpl<Long>() {
    }).defaultValue(30000L).describe("").verifier(aLong -> aLong > 0).build();
    public static final ConfigKey<Boolean> LOCAL_FILE_LISTEN = ConfigKey.ConfigKeyBuilder.builder("localfile.listen", new TypeReferenceImpl<Boolean>() {
    }).defaultValue(false).describe("").build();
}
