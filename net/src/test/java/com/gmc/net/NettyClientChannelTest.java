package com.gmc.net;

import com.gmc.config.Config;
import com.gmc.config.ConfigProperty;
import com.gmc.config.process.DefaultValueProcessor;
import com.gmc.core.Tuple2;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.net.InetSocketAddress;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;

import static org.junit.jupiter.api.Assertions.*;

class NettyClientChannelTest {
    @Timeout(10)
    @Test
    void testConnect() throws Exception {

        ConfigProperty configProperty = Config.builder().paths("classpath://com/gmc/net/serverConfig.properties").configClass(ServerConfig.class).processor(new DefaultValueProcessor()).build().getConfig();

        MessageCodeManager messageCodeManager = NettyMessageCodeManagerBuilder.builder().
                addMessageDistinguish(new TestMessage.TestMessageHead().getType(), new TestMessage.TestMessageHead().getVersion(), TestMessage::new)
                .setReleaseByteBufferForDecode(false)
                .build();
        NettyServerTest.TestServerMessageManager testServerMessageManager = new NettyServerTest.TestServerMessageManager();
        NettyServer nettyServer = new NettyServer(configProperty, messageCodeManager, testServerMessageManager);

        ConfigProperty clientConfigProperty = Config.builder().paths("classpath://com/gmc/net/clientConfig.properties").configClass(ClientConfig.class).processor(new DefaultValueProcessor()).build().getConfig();
        NettyClient client = new NettyClient(clientConfigProperty, () -> new HeartbeatMessage(), message -> ((long) ((DefaultMessageHead) message.getHead()).getAutoIncrId()), (message, index) -> ((DefaultMessageHead) message.getHead()).setAutoIncrId(index.intValue()), messageCodeManager);

        NettyClientChannel nettyClientChannel = new NettyClientChannel(client);
        nettyClientChannel.init();
        InetSocketAddress remote = new InetSocketAddress(configProperty.getConfig(ServerConfig.SERVER_HOST), configProperty.getConfig(ServerConfig.SERVER_PORT));
        //testConnect
        int size = 5;
        CompletableFuture<Tuple2<Integer, Client.Channel>>[] completableFutures = new CompletableFuture[size];
        for (int i = 0; i < size; i++) {
            completableFutures[i] = new CompletableFuture<>();
            int finalI = i;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    nettyClientChannel.connect(remote, 0).handle(new BiFunction<Tuple2<Integer, Client.Channel>, Throwable, Object>() {
                        @Override
                        public Object apply(Tuple2<Integer, Client.Channel> tuple2, Throwable throwable) {
                            if (throwable != null) {
                                completableFutures[finalI].completeExceptionally(throwable);
                            } else {
                                completableFutures[finalI].complete(tuple2);
                            }
                            return null;
                        }
                    });
                }
            }).start();
        }
        AtomicInteger connectIndex = new AtomicInteger(-1);
        TestUtils.waitForTrue(() -> {
            for (int i = 0; i < size; i++) {
                if (completableFutures[i].isDone()) {
                    if (connectIndex.get() >= 0) {
                        throw new IllegalArgumentException();
                    }
                    connectIndex.set(i);
                }
            }
            return connectIndex.get() >= 0;
        });


        Tuple2<Integer, Client.Channel> tuple2 = new Tuple2<>();
        assertTrue(TestUtils.waitForTrue(() -> Optional.ofNullable(nettyClientChannel.connectListeners.map.get(1)).map(v -> v.listeners.size()).orElse(-1) == size - 1));
        assertTrue(connectIndex.get() >= 0);
        int finalConnectIndex = connectIndex.get();
        boolean throwException = true;
        try {
            Tuple2<Integer, Client.Channel> tuple22 = completableFutures[finalConnectIndex].get();
            throwException = false;
        }catch (ExecutionException e){
            assertThrows(ConnectFailException.class, () -> {throw e.getCause();});
        }
        assertTrue(throwException);

        nettyServer.start();

        nettyClientChannel.connect(remote, 0).get();
        for (int i = 0; i < size; i++) {
            if (i != connectIndex.get()) {
                Tuple2<Integer, Client.Channel> tuple21 = completableFutures[i].get();
                assertTrue(tuple21.getV1() == 1);

            }
        }

        nettyServer.close();
        nettyClientChannel.close();


    }


}