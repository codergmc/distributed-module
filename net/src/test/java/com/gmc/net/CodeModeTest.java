package com.gmc.net;

import io.netty.buffer.ByteBufAllocator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class CodeModeTest {
    @ParameterizedTest
    @MethodSource("codeModeProvider")
    void testCodeMode(CodeMode codeMode) {
        BytesBufferAllocate bytesBufferAllocate = new NettyBytesBufferAllocate(ByteBufAllocator.DEFAULT);
        BytesBuffer bytesBuffer = bytesBufferAllocate.allocateAutoIncr(16, false);
        TestMessage.TestMessageBody testMessageBody = new TestMessage.TestMessageBody(1, (short) 2, 3L);
        TestMessage.TestMessageBody testMessageBody1 = new TestMessage.TestMessageBody();
        codeMode.encode(testMessageBody, bytesBuffer);
        bytesBuffer.markReadIndex();
        codeMode.decode(testMessageBody1, bytesBuffer);
        assertEquals(bytesBuffer.readIndex(), bytesBuffer.writeIndex());
        bytesBuffer.resetReadIndex();
        assertEquals((codeMode.extraBytes() + testMessageBody.size()), bytesBuffer.readableBytes());
        BytesBuffer unwrap = codeMode.unwrap(bytesBuffer);
        assertEquals(unwrap, bytesBuffer.sliceIndex(codeMode.extraBytes(), bytesBuffer.writeIndex() - codeMode.extraBytes()));

        bytesBuffer.release();
        assertEquals(testMessageBody, testMessageBody1);

    }

    static Stream<CodeMode> codeModeProvider() {
        return Stream.of(new AllSizeCodeMode(4), new SizeCodeMode(4), new DefaultCodeMode(), new AllSizeCodeMode(2), new SizeCodeMode(2));
    }


}