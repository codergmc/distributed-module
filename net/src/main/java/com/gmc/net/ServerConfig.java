package com.gmc.net;

import com.gmc.config.ConfigKey;
import com.gmc.config.TypeReferenceImpl;

import java.util.Random;
import java.util.concurrent.ForkJoinPool;

public class ServerConfig {
    public static final ConfigKey<Integer> WORK_THREAD_NUM = ConfigKey.ConfigKeyBuilder.builder("server.workThreadNum", new TypeReferenceImpl<Integer>() {
    }).
            defaultValue(Runtime.getRuntime().availableProcessors()).describe("work thread number").build();

    public static final ConfigKey<String> SERVER_HOST = ConfigKey.ConfigKeyBuilder.builder("server.host", new TypeReferenceImpl<String>() {
    }).
            defaultValue("localhost").describe("server host").build();


    public static final ConfigKey<Integer> SERVER_PORT = ConfigKey.ConfigKeyBuilder.builder("server.port", new TypeReferenceImpl<Integer>() {
    }).
            defaultValue(9999).describe("server port").build();
}
