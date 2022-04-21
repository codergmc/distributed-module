package com.gmc.net;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DefaultMessageCodeManagerTest {
    @Test
    void testCode() {
        MessageCodeManager messageCodeManager = NettyMessageCodeManagerBuilder.builder().addMessageDistinguish(new TestMessage.TestMessageHead().getType(), new TestMessage.TestMessageHead().getVersion(), () -> new TestMessage()).build();
        TestMessage testMessage = new TestMessage(new TestMessage.TestMessageHead(), new TestMessage.TestMessageBody(1, (short) 2, 3));
        testMessage.setMessageCodeManager(messageCodeManager);
        BytesBuffer bytesBuffer = messageCodeManager.encode(testMessage);
        assertTrue(bytesBuffer.readableBytes() == testMessage.size() + messageCodeManager.allCodeMode().extraBytes() + messageCodeManager.headCodeMode().extraBytes() + messageCodeManager.bodyCodeMode().extraBytes());
        Message decode = messageCodeManager.decode(bytesBuffer);
        assertTrue(decode.equals(testMessage));
        assertTrue(bytesBuffer.readIndex()==bytesBuffer.writeIndex());

    }

}