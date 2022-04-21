package com.gmc.net;

import io.netty.buffer.AbstractReferenceCountedByteBuf;
import io.netty.buffer.ByteBuf;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import javax.imageio.plugins.tiff.BaselineTIFFTagSet;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.function.BiFunction;

import static org.junit.jupiter.api.Assertions.*;

class NettyBytesBufferTest {
    NettyBytesBuffer bytesBuffer;

    @BeforeEach
    void setUp() {
        bytesBuffer = (NettyBytesBuffer) new NettyBytesBufferAllocate().allocateAutoIncr(100);
    }

    @AfterEach
    void tearDown() {
        bytesBuffer.release();
    }

    @Test
    void writeChar() throws NoSuchMethodException {
        Character value = 'c';
        test(value, BytesBuffer.class.getMethod("writeChar", char.class), BytesBuffer.class.getMethod("readChar"), Character.BYTES, (a, b) -> a.equals(b));
    }

    void test(Object value, Method writeMethod, Method readMethod, int writeSize, BiFunction<Object, Object, Boolean> eq) {
        int index = bytesBuffer.writeIndex();
        try {
            writeMethod.invoke(bytesBuffer, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        Object read = bytesBuffer.readAndResetReadIndex(index, buffer -> {
            try {
                return readMethod.invoke(buffer);
            } catch (Exception e) {
                try {
                    return readMethod.invoke(buffer, writeSize);
                } catch (Exception e1) {
                    throw new RuntimeException(e1);
                }

            }
        });
        assertTrue(eq.apply(value, read));
        assertEquals(writeSize + index, bytesBuffer.writeIndex());
    }

    @Test
    void writeLong() throws NoSuchMethodException {
        Long value = 19l;
        test(value, BytesBuffer.class.getMethod("writeLong", long.class), BytesBuffer.class.getMethod("readLong"), Long.BYTES, (a, b) -> a.equals(b));
    }

    @Test
    void writeDouble() throws NoSuchMethodException {
        Double value = 19.1d;
        test(value, BytesBuffer.class.getMethod("writeDouble", double.class), BytesBuffer.class.getMethod("readDouble"), Double.BYTES, (a, b) -> a.equals(b));
    }

    @Test
    void writeShort() throws NoSuchMethodException {
        Short value = 11;
        test(value, BytesBuffer.class.getMethod("writeShort", short.class), BytesBuffer.class.getMethod("readShort"), Short.BYTES, (a, b) -> a.equals(b));
    }

    @Test
    void writeInt() throws NoSuchMethodException {
        Integer value = 19;
        test(value, BytesBuffer.class.getMethod("writeInt", int.class), BytesBuffer.class.getMethod("readInt"), Integer.BYTES, (a, b) -> a.equals(b));
    }

    @Test
    void writeByte() throws NoSuchMethodException {
        Byte value = 19;
        test(value, BytesBuffer.class.getMethod("writeByte", byte.class), BytesBuffer.class.getMethod("readByte"), Byte.BYTES, (a, b) -> a.equals(b));
    }

    @Test
    void writeBytes() throws NoSuchMethodException {
        byte[] bytes = new byte[]{1, 2, 3};
        BiFunction<Object, Object, Boolean> objectObjectBooleanBiFunction = (BiFunction<Object, Object, Boolean>) (a, b) -> {
            byte[] a1 = (byte[]) a;
            byte[] b1 = (byte[]) b;
            if (a1.length == b1.length) {
                for (int i = 0; i < a1.length; i++) {
                    if (a1[i] != b1[i]) {
                        return false;
                    }
                }
            } else return false;
            return true;
        };
        test(bytes, BytesBuffer.class.getMethod("writeBytes", byte[].class), BytesBuffer.class.getMethod("readBytes", int.class), bytes.length, objectObjectBooleanBiFunction);
    }

    @Test
    void writeFloat() throws NoSuchMethodException {
        float value = 19.1f;
        test(value, BytesBuffer.class.getMethod("writeFloat", float.class), BytesBuffer.class.getMethod("readFloat"), Float.BYTES, (a, b) -> a.equals(b));
    }


    @Test
    void readIndex() {
        bytesBuffer.writeInt(1);
        bytesBuffer.writeInt(2);
        bytesBuffer.readIndex(4);
        assertEquals(2, bytesBuffer.readInt());
    }


    @Test
    void writeIndex() {
        bytesBuffer.writeInt(1);
        bytesBuffer.writeInt(2);
        bytesBuffer.writeIndex(0);
        bytesBuffer.writeInt(3);
        assertEquals(3, bytesBuffer.readInt());
    }

    @Test
    void actualSize() {
        ByteBuf innerByteBuf = bytesBuffer.getInnerByteBuf();
        int length = -1;
        Class c = innerByteBuf.getClass();
        while (c != Object.class) {
            try {
                Field maxLength = c.getDeclaredField("maxLength");
                maxLength.setAccessible(true);
                length = (int) maxLength.get(innerByteBuf);
                break;
            } catch (Exception e) {
                c = c.getSuperclass();
                continue;
            }
        }
        if (length == -1) {
            length = innerByteBuf.capacity();
        }
        assertEquals(bytesBuffer.actualSize(), length);
    }

    @Test
    void requireSize() {
        ByteBuf innerByteBuf = bytesBuffer.getInnerByteBuf();
        assertEquals(innerByteBuf.capacity(), bytesBuffer.requireSize());
    }

    @Test
    void release() {
        ByteBuf innerByteBuf = bytesBuffer.getInnerByteBuf();
        innerByteBuf.retain();
        bytesBuffer.release();
        AbstractReferenceCountedByteBuf innerByteBuf1 = (AbstractReferenceCountedByteBuf) innerByteBuf;
        assertEquals(1, innerByteBuf1.refCnt());
    }

    @Test
    void markReadIndex() {
        bytesBuffer.writeInt(1);
        bytesBuffer.writeInt(2);
        bytesBuffer.markReadIndex();
        bytesBuffer.readIndex(4);
        bytesBuffer.resetReadIndex();
        assertEquals(0, bytesBuffer.readIndex());
    }

    @Test
    void markWriteIndex() {
        bytesBuffer.writeInt(1);
        bytesBuffer.markWriteIndex();
        bytesBuffer.writeInt(2);
        bytesBuffer.resetWriteIndex();
        assertEquals(4, bytesBuffer.writeIndex());
    }


    @Test
    void sliceIndex() {
        bytesBuffer.writeInt(1);
        bytesBuffer.writeInt(2);
        assertEquals(2, bytesBuffer.sliceIndex(4, 4).readInt());
    }

    @Test
    void writeIntAndResetWriteIndex() {
        bytesBuffer.writeInt(1);
        bytesBuffer.writeInt(2);
        int index = bytesBuffer.writeIndex();
        bytesBuffer.writeIntAndResetWriteIndex(4, 3);
        bytesBuffer.readIndex(4);
        assertEquals(3, bytesBuffer.readInt());
        assertEquals(index, bytesBuffer.writeIndex());
    }

    @Test
    void writeShortAndResetWriteIndex() {
        bytesBuffer.writeShort((short) 1);
        bytesBuffer.writeShort((short) 2);
        int index = bytesBuffer.writeIndex();
        bytesBuffer.writeShortAndResetWriteIndex(2, (short) 3);
        bytesBuffer.readIndex(2);
        assertEquals(3, bytesBuffer.readShort());
        assertEquals(index, bytesBuffer.writeIndex());
    }

    @Test
    void writeByteAndResetWriteIndex() {
        bytesBuffer.writeByte((byte) 1);
        bytesBuffer.writeByte((byte) 2);
        int index = bytesBuffer.writeIndex();
        bytesBuffer.writeByteAndResetWriteIndex(1, (byte) 3);
        bytesBuffer.readIndex(1);
        assertEquals(3, bytesBuffer.readByte());
        assertEquals(index, bytesBuffer.writeIndex());
    }

    @Test
    void writeLongAndResetWriteIndex() {
        bytesBuffer.writeLong(1);
        bytesBuffer.writeLong(2);
        int index = bytesBuffer.writeIndex();
        bytesBuffer.writeLongAndResetWriteIndex(8, 3L);
        bytesBuffer.readIndex(8);
        assertEquals(3, bytesBuffer.readLong());
        assertEquals(index, bytesBuffer.writeIndex());
    }

    @Test
    void writeDoubleAndResetWriteIndex() {
        bytesBuffer.writeDouble(1d);
        bytesBuffer.writeDouble(2d);
        int index = bytesBuffer.writeIndex();
        bytesBuffer.writeDoubleAndResetWriteIndex(8, 3d);
        bytesBuffer.readIndex(8);
        assertEquals(3d, bytesBuffer.readDouble());
        assertEquals(index, bytesBuffer.writeIndex());
    }

    @Test
    void writeFloatAndResetWriteIndex() {
        bytesBuffer.writeFloat(1);
        bytesBuffer.writeFloat(2);
        int index = bytesBuffer.writeIndex();
        bytesBuffer.writeFloatAndResetWriteIndex(4, 3);
        bytesBuffer.readIndex(4);
        assertEquals(3, bytesBuffer.readFloat());
        assertEquals(index, bytesBuffer.writeIndex());
    }


    @Test
    void writeConsumerAndResetWriteIndex() {
        bytesBuffer.writeInt(1);
        bytesBuffer.writeInt(2);
        int index = bytesBuffer.writeIndex();
        bytesBuffer.writeConsumerAndResetWriteIndex(4, buffer -> buffer.writeInt(3));
        bytesBuffer.readIndex(4);
        assertEquals(3, bytesBuffer.readInt());
        assertEquals(index, bytesBuffer.writeIndex());

    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 4})
    void writeConsumerSize(int sizeLength) {
        bytesBuffer.writeInt(1);
        bytesBuffer.writeInt(2);
        int index = bytesBuffer.writeIndex();
        bytesBuffer.writeConsumerSize(buffer -> buffer.writeInt(3), sizeLength);
        assertEquals(bytesBuffer.writeIndex(), index + sizeLength + 4);
        bytesBuffer.readIndex(8);
        if (sizeLength == 4) {
            assertEquals(4, bytesBuffer.readInt());

        } else if (sizeLength == 2) {
            assertEquals(4, bytesBuffer.readShort());
        } else if(sizeLength==1){
            assertEquals(4, bytesBuffer.readByte());
        } else throw new RuntimeException();
        assertEquals(3, bytesBuffer.readInt());
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 4})
    void writeSize(int sizeLength) {
        bytesBuffer.writeInt(1);
        bytesBuffer.writeInt(2);
        int index = bytesBuffer.writeIndex();
        bytesBuffer.writeSizeResetWriteIndex(4, 4, sizeLength);
        assertEquals(bytesBuffer.writeIndex(), index);
        bytesBuffer.readIndex(4);
        int result = -1;
        if (sizeLength == 4) {
            result = bytesBuffer.readInt();
        } else if (sizeLength == 2) {
            result = bytesBuffer.readShort();
        } else if (sizeLength == 1) {
            result = bytesBuffer.readByte();
        }
        assertEquals(result, 4);

    }

    @Test
    void readAndResetReadIndex() {
        bytesBuffer.writeInt(1);
        bytesBuffer.writeInt(2);
        int index = bytesBuffer.readIndex();
        Integer integer = bytesBuffer.readAndResetReadIndex(4, buffer -> {
            return buffer.readInt();
        });
        assertEquals(2, (int) integer);
        assertEquals(index, bytesBuffer.readIndex());
    }


    @ParameterizedTest
    @ValueSource(ints = {1, 2, 4})
    void writeAllConsumerSize(int sizeLength) {
        bytesBuffer.writeInt(1);
        bytesBuffer.writeInt(2);
        bytesBuffer.writeAllConsumerSize(buffer -> {
            bytesBuffer.writeInt(3);
        }, sizeLength);
        assertEquals(12 + sizeLength, bytesBuffer.writeIndex());
        bytesBuffer.readIndex(8);
        int i=-1;
        if (sizeLength==4) {
             i = bytesBuffer.readInt();
        } else if(sizeLength==2){
            i=bytesBuffer.readShort();
        } else if(sizeLength==1){
            i=bytesBuffer.readByte();
        } else throw new RuntimeException();
        assertEquals(i, 4 + sizeLength);
    }



}