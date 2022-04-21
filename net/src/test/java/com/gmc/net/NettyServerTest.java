package com.gmc.net;

import com.gmc.config.Config;
import com.gmc.config.ConfigProperty;
import com.gmc.config.process.DefaultValueProcessor;
import io.netty.util.ResourceLeakDetector;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class NettyServerTest {

    @Test
    @RepeatedTest(10)
    void test() throws Exception {
        ConfigProperty configProperty = Config.builder().paths("classpath://com/gmc/net/serverConfig.properties").configClass(ServerConfig.class).processor(new DefaultValueProcessor()).build().getConfig();

        MessageCodeManager messageCodeManager = NettyMessageCodeManagerBuilder.builder().
                addMessageDistinguish(new TestMessage.TestMessageHead().getType(), new TestMessage.TestMessageHead().getVersion(), TestMessage::new)
                .setReleaseByteBufferForDecode(false)
                .build();
        TestServerMessageManager testServerMessageManager = new TestServerMessageManager();
        NettyServer nettyServer = new NettyServer(configProperty, messageCodeManager, testServerMessageManager);

        nettyServer.start();
        Client client = new Client((DefaultMessageCodeManager) messageCodeManager);
        client.connect(configProperty.getConfig(ServerConfig.SERVER_HOST), configProperty.getConfig(ServerConfig.SERVER_PORT));
        TestMessage testMessage = new TestMessage(new TestMessage.TestMessageBody(1, (short) 2, 3));
        client.send(testMessage);
        Message message = testServerMessageManager.getMessageWrap().get(10, TimeUnit.SECONDS).getMessage();
        assertEquals(testMessage, message);
        Message receive = client.receive();
        assertEquals(testMessage, receive);
        client.close();
        Boolean aBoolean = testServerMessageManager.channelCloseCall.get(10, TimeUnit.SECONDS);
        assertTrue(aBoolean);

        nettyServer.close();


    }

    class Client {
        SocketChannel socketChannel;
        DefaultMessageCodeManager messageCodeManager;
        ByteBuffer buffer;
        BytesBuffer bytesBuffer;

        Client(DefaultMessageCodeManager messageCodeManager) throws IOException {
            this.messageCodeManager = messageCodeManager;
            socketChannel = SocketChannel.open();
            socketChannel.configureBlocking(false);
            socketChannel.setOption(StandardSocketOptions.TCP_NODELAY, true);
            socketChannel.setOption(StandardSocketOptions.SO_LINGER, 0);
            socketChannel.bind(new InetSocketAddress("localhost", 8888));
            buffer = ByteBuffer.allocate(128);

            bytesBuffer = messageCodeManager.getAllocate().allocateAutoIncr(128);
        }

        public void connect(String host, int port) {
            try {

                socketChannel.connect(new InetSocketAddress(host, port));
                int count = 100;
                while (!socketChannel.finishConnect() && count > 0) {
                    TimeUnit.MILLISECONDS.sleep(100);
                    count--;
                }
                if (count == 0) {
                    throw new RuntimeException();
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        public void send(Message message) {
            send(messageCodeManager.encode(message));
        }

        public void close() {
            try {
                socketChannel.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }finally {
                buffer.clear();
                bytesBuffer.release();
            }
        }

        public void send(BytesBuffer buffer) {
            ByteBuffer transform = transform(buffer);
            int capacity = transform.capacity();
            while (capacity > 0) {
                try {
                    capacity -= socketChannel.write(transform);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

        }

        public Message receive() {

            while (true) {
                try {
                    socketChannel.read(buffer);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                transform(buffer, bytesBuffer);
                bytesBuffer.markReadIndex();
                try {
                    Message decode = messageCodeManager.decode(bytesBuffer);
                    bytesBuffer.discardReadBytes();
                    return decode;
                } catch (Exception exception) {
                    bytesBuffer.resetReadIndex();
                    continue;
                }
            }
        }

        private void transform(ByteBuffer buffer, BytesBuffer bytesBuffer) {
            buffer.flip();
            for (int i = 0; i < buffer.limit() - buffer.position(); i++) {
                bytesBuffer.writeByte(buffer.get(i));
            }
            buffer.clear();


        }

        private ByteBuffer transform(BytesBuffer buffer) {
            ByteBuffer allocate = ByteBuffer.allocate(buffer.readableBytes());
            int length = buffer.readableBytes();
            for (int i = 0; i < length; i++) {
                allocate.put(buffer.readByte());
            }
            allocate.flip();
            return allocate;

        }
    }

    static class TestServerMessageManager implements ServerMessageManager {
        volatile CompletableFuture<MessageWrap> messageWrap = new CompletableFuture<>();
        volatile CompletableFuture<Boolean> channelCloseCall = new CompletableFuture<>();

        @Override
        public void receiveMessage(MessageWrap messageWrap) {
            this.messageWrap.complete(messageWrap);
            messageWrap.channel.write(messageWrap.getMessage());
            messageWrap.channel.flush();

        }

        @Override
        public void channelClose(Server.Channel channel) {
            channelCloseCall.complete(true);
        }

        @Override
        public void channelActive(Server.Channel channel) {

        }

        public CompletableFuture<MessageWrap> getMessageWrap() {
            return messageWrap;
        }

        public CompletableFuture<Boolean> isChannelCloseCall() {
            return channelCloseCall;
        }
    }


}