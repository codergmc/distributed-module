package com.gmc.net;

import com.gmc.config.Config;
import com.gmc.config.ConfigKey;
import com.gmc.config.ConfigProperty;
import com.gmc.config.TypeReference;
import com.gmc.config.TypeReferenceImpl;
import com.gmc.config.validation.ComparableVerifierBuilder;

import java.nio.file.FileAlreadyExistsException;

public class ClientConfig {
    public static final ConfigKey<Integer> HEARTBEAT_TIME_OUT = ConfigKey.ConfigKeyBuilder.builder("client.heartbeat.timeout", new TypeReferenceImpl<Integer>() {
    }).describe("heartbeat timeout in millisecond,if channel have not receive message from server in this time,client should be closed ").defaultValue(20000).verifier((ComparableVerifierBuilder.builder(Integer.class)).greaterThan(0, false).build()).build();

    public static final ConfigKey<Integer> HEARTBEAT_TIME_SEND_OUT = ConfigKey.ConfigKeyBuilder.builder("client.heartbeat.send.timeout", new TypeReferenceImpl<Integer>() {
    }).describe("heartbeat  send timeout in millisecond,f channel have not receive message from server in this time,client should send heartbeat message ").defaultValue(10000).verifier((ComparableVerifierBuilder.builder(Integer.class)).greaterThan(0, false).build()).build();


    public static final ConfigKey<Integer> CONNECT_WAIT_TIME_OUT = ConfigKey.ConfigKeyBuilder.builder("client.connectWaitTimeout", new TypeReferenceImpl<Integer>() {
    }).describe("connect wait timeout in millisecond").defaultValue(20000).verifier((ComparableVerifierBuilder.builder(Integer.class)).greaterThan(0, false).build()).build();


    public static final ConfigKey<Boolean> AUTO_RECONNECT = ConfigKey.ConfigKeyBuilder.builder("client.autoConnect", new TypeReferenceImpl<Boolean>() {
    }).describe("auto reconnect").defaultValue(true).build();

    public static final ConfigKey<Boolean> MESSAGE_ID_AUTO_INCREASE = ConfigKey.ConfigKeyBuilder.builder("client.messageIdAutoIncrease", new TypeReferenceImpl<Boolean>() {
    }).describe("message id auto increase").defaultValue(true).build();

    public static final ConfigKey<Boolean> MESSAGE_SEND_BATCH = ConfigKey.ConfigKeyBuilder.builder("client.send.batch", new TypeReferenceImpl<Boolean>() {
    }).describe("message send batch").defaultValue(true).build();

    public static final ConfigKey<Integer> FLUSH_TIME_INTERVAL = ConfigKey.ConfigKeyBuilder.builder("client.flush.time.interval", new TypeReferenceImpl<Integer>() {
    }).describe("the time interval of invoke flush ").defaultValue(1000).verifier((ComparableVerifierBuilder.builder(Integer.class)).greaterThan(0, false).build()).build();

    public static final ConfigKey<Integer> FLUSH_COUNT_INTERVAL = ConfigKey.ConfigKeyBuilder.builder("client.flush.count.interval", new TypeReferenceImpl<Integer>() {
    }).describe("the message count interval of invoke flush ").defaultValue(5).verifier((ComparableVerifierBuilder.builder(Integer.class)).greaterThan(0, false).build()).build();

}
