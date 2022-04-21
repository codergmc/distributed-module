package com.gmc.net;

import com.gmc.config.Config;
import com.gmc.config.ConfigProperty;
import com.gmc.config.process.DefaultValueProcessor;
import org.junit.jupiter.api.Test;

import java.net.InetSocketAddress;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;

class NettyClientTest {

    @Test
    void test() throws Exception {
        NettyServer nettyServer;
        TestServerMessageManager testServerMessageManager = new TestServerMessageManager();
        ConfigProperty serverConfigProperty = Config.builder().paths("classpath://com/gmc/net/serverConfig.properties").configClass(ServerConfig.class).processor(new DefaultValueProcessor()).build().getConfig();
        MessageCodeManager messageCodeManager = NettyMessageCodeManagerBuilder.builder().
                addMessageDistinguish(new TestMessage.TestMessageHead().getType(), new TestMessage.TestMessageHead().getVersion(), TestMessage::new)
                .build();
        nettyServer = new NettyServer(serverConfigProperty, messageCodeManager, testServerMessageManager);

        nettyServer.start();
        ConfigProperty clientConfigProperty = Config.builder().paths("classpath://com/gmc/net/clientConfig.properties").configClass(ClientConfig.class).processor(new DefaultValueProcessor()).build().getConfig();
        NettyClient client = new NettyClient(clientConfigProperty, () -> new HeartbeatMessage(), message -> ((long) ((DefaultMessageHead) message.getHead()).getAutoIncrId()), (message, index) -> ((DefaultMessageHead) message.getHead()).setAutoIncrId(index.intValue()), messageCodeManager);
        client.start();
        client.connectAsync(new InetSocketAddress(serverConfigProperty.getConfig(ServerConfig.SERVER_HOST), serverConfigProperty.getConfig(ServerConfig.SERVER_PORT))).get(10, TimeUnit.SECONDS);
        TestMessage testMessage = new TestMessage(new TestMessage.TestMessageBody(1, (short) 2, 3));
        CompletableFuture<Void> send = new CompletableFuture<>();
        CompletableFuture<TestMessage> receive = new CompletableFuture<>();
        client.send(testMessage, new Client.SendNotify() {
            @Override
            public void hasSent() {
                send.complete(null);
            }

            @Override
            public void onException(Throwable throwable) {
                send.completeExceptionally(throwable);
            }
        }, new Client.ReceiveNotify() {
            @Override
            public void receive(Readable readable) {
                receive.complete((TestMessage) readable);
            }

            @Override
            public void onException(Throwable throwable) {
                receive.completeExceptionally(throwable);
            }
        });
        send.get(10, TimeUnit.SECONDS);
        assertTrue(testMessage.equals(receive.get(10, TimeUnit.SECONDS)));
        client.close();
        nettyServer.close();

    }

    @Test
    void testReconnect() throws Exception {
        NettyServer nettyServer;
        TestServerMessageManager testServerMessageManager = new TestServerMessageManager();
        ConfigProperty serverConfigProperty = Config.builder().paths("classpath://com/gmc/net/serverConfig.properties").configClass(ServerConfig.class).processor(new DefaultValueProcessor()).build().getConfig();
        MessageCodeManager messageCodeManager = NettyMessageCodeManagerBuilder.builder().
                addMessageDistinguish(new TestMessage.TestMessageHead().getType(), new TestMessage.TestMessageHead().getVersion(), TestMessage::new)
                .build();
        nettyServer = new NettyServer(serverConfigProperty, messageCodeManager, testServerMessageManager);
        nettyServer.start();
        ConfigProperty clientConfigProperty = Config.builder().paths("classpath://com/gmc/net/clientConfig.properties").configClass(ClientConfig.class).processor(new DefaultValueProcessor()).build().getConfig();
        NettyClient client = new NettyClient(clientConfigProperty, () -> new HeartbeatMessage(), message -> ((long) ((DefaultMessageHead) message.getHead()).getAutoIncrId()), (message, index) -> ((DefaultMessageHead) message.getHead()).setAutoIncrId(index.intValue()), messageCodeManager);
        client.start();
        client.connectAsync(new InetSocketAddress(serverConfigProperty.getConfig(ServerConfig.SERVER_HOST), serverConfigProperty.getConfig(ServerConfig.SERVER_PORT))).get(10, TimeUnit.SECONDS);

        assertTrue(TestUtils.waitForTrue(10, TimeUnit.SECONDS, () -> {
            Boolean aBoolean = testServerMessageManager.channelClose.get(0);
            return aBoolean != null && !aBoolean;

        }));
        assertTrue(TestUtils.waitForTrue(10, TimeUnit.SECONDS, () -> {
            Server.Channel channel = testServerMessageManager.channel.get(0);
            return channel != null;

        }));


        testServerMessageManager.channel.get(0).close().get(10, TimeUnit.SECONDS);
        assertTrue(TestUtils.waitForTrue(10, TimeUnit.SECONDS, () -> testServerMessageManager.channelClose.get(1)));
        assertTrue(TestUtils.waitForTrue(10, TimeUnit.SECONDS, () -> testServerMessageManager.channel.get(1) == null));
        assertTrue(TestUtils.waitForTrue(clientConfigProperty.getConfig(ClientConfig.CONNECT_WAIT_TIME_OUT) / 1000 + 1, TimeUnit.SECONDS, () -> testServerMessageManager.channel.get(2) != null));
        assertTrue(TestUtils.waitForTrue(clientConfigProperty.getConfig(ClientConfig.CONNECT_WAIT_TIME_OUT) / 1000 + 1, TimeUnit.SECONDS, () -> !testServerMessageManager.channelClose.get(2)));
        nettyServer.close();

        assertTrue(TestUtils.waitForTrue(clientConfigProperty.getConfig(ClientConfig.CONNECT_WAIT_TIME_OUT) / 1000 + 1, TimeUnit.SECONDS, () -> testServerMessageManager.channel.get(3) == null));
        assertTrue(TestUtils.waitForTrue(clientConfigProperty.getConfig(ClientConfig.CONNECT_WAIT_TIME_OUT) / 1000 + 1, TimeUnit.SECONDS, () -> testServerMessageManager.channelClose.get(3)));

        assertTrue(!TestUtils.waitForTrue(clientConfigProperty.getConfig(ClientConfig.CONNECT_WAIT_TIME_OUT) / 1000 + 1, TimeUnit.SECONDS, () -> testServerMessageManager.channel.get(4) != null));
        assertTrue(!TestUtils.waitForTrue(clientConfigProperty.getConfig(ClientConfig.CONNECT_WAIT_TIME_OUT) / 1000 + 1, TimeUnit.SECONDS, () -> Optional.ofNullable(testServerMessageManager.channelClose.get(4)).orElse(false)));

        client.close();


    }


    class TestServerMessageManager implements ServerMessageManager {

        AtomicEpochHistoryReference<Server.Channel> channel = new AtomicEpochHistoryReference<>();
        AtomicEpochHistoryReference<Boolean> channelClose = new AtomicEpochHistoryReference<>();

        @Override
        public void receiveMessage(MessageWrap messageWrap) {

            messageWrap.channel.write(messageWrap.getMessage());
            messageWrap.channel.flush();

        }

        @Override
        public void channelClose(Server.Channel channel) {
            channelClose.set(true);
            this.channel.set(null);
        }

        @Override
        public void channelActive(Server.Channel channel) {
            channelClose.set(false);
            this.channel.set(channel);
        }

    }

}