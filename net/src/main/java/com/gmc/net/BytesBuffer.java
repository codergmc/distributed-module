package com.gmc.net;

import java.util.function.Consumer;
import java.util.function.Function;

public interface BytesBuffer {


    void writeChar(char value);

    void writeLong(long value);

    void writeDouble(double value);

    void writeShort(short value);

    void writeInt(int value);

    void writeByte(byte value);

    void writeBytes(byte[] bytes);

    void writeFloat(float value);

    void readBytes(byte[] bytes);

    default void writeIntAndResetWriteIndex(int index, int value) {
        writeConsumerAndResetWriteIndex(index, (ignore -> writeInt(value)));
    }

    default void writeShortAndResetWriteIndex(int index, short value) {
        writeConsumerAndResetWriteIndex(index, (ignore -> writeShort(value)));
    }

    default void writeByteAndResetWriteIndex(int index, byte value) {
        writeConsumerAndResetWriteIndex(index, (ignore -> writeByte(value)));

    }

    default void writeChar(int index, char value) {
        writeConsumerAndResetWriteIndex(index, (ignore -> writeChar(value)));
    }

    default void writeLongAndResetWriteIndex(int index, long value) {
        writeConsumerAndResetWriteIndex(index, (ignore -> writeLong(value)));

    }

    default void writeDoubleAndResetWriteIndex(int index, double value) {
        writeConsumerAndResetWriteIndex(index, (ignore -> writeDouble(value)));

    }

    default void writeFloatAndResetWriteIndex(int index, float value) {
        writeConsumerAndResetWriteIndex(index, (ignore -> writeFloat(value)));
    }

    int readInt();

    short readShort();

    byte readByte();

    char readChar();

    long readLong();

    double readDouble();

    float readFloat();

    int readIndex();

    void readIndex(int readIndex);

    int writeIndex();

    void writeIndex(int writeIndex);

    /**
     * if you need a 120 bytes buffer,but actually allocate 128 bytes for some optimizes.
     * invoke actualSize will return 128,invoke requireSize will return 120
     *
     * @return
     */
    int actualSize();

    int requireSize();

    void release();

    void markReadIndex();

    default void writeConsumerAndResetWriteIndex(int writeIndex, Consumer<BytesBuffer> consumer) {
        int curWriteIndex = writeIndex();
        writeIndex(writeIndex);
        consumer.accept(this);
        writeIndex(curWriteIndex);
    }

    void markWriteIndex();

    void resetReadIndex();

    void resetWriteIndex();
    void discardReadBytes();
    /**
     * write size    size = consumer write size
     * write consumer
     *
     * @param consumer
     * @param sizeLength
     */
    default void writeConsumerSize(Consumer<BytesBuffer> consumer, int sizeLength) {
        ExceptionUtils.check(sizeLength <= 4, "");
        int sizeIndex = writeIndex();
        writeSizeResetWriteIndex(sizeIndex, 0, sizeLength);
        writeIndex(writeIndex() + sizeLength);
        int begin = writeIndex();
        consumer.accept(this);
        int end = writeIndex();
        writeSizeResetWriteIndex(sizeIndex, end - begin, sizeLength);
    }

    default void writeSizeResetWriteIndex(int sizeIndex, int size, int sizeLength) {
        if (sizeLength == 4) {
            writeIntAndResetWriteIndex(sizeIndex, size);
        } else if (sizeLength == 2) {
            writeShortAndResetWriteIndex(sizeIndex, (short) size);
        } else if (sizeLength == 1) {
            writeByteAndResetWriteIndex(sizeIndex, (byte) size);
        } else throw new IllegalArgumentException();
    }

    default void readAndResetReadIndex(int readIndex, Consumer<BytesBuffer> consumer) {
        this.readAndResetReadIndex(readIndex, buffer -> {
            consumer.accept(buffer);
            return null;
        });
    }

    default void readAndResetReadIndex(Consumer<BytesBuffer> consumer) {
        this.readAndResetReadIndex(readIndex(), consumer);
    }

    default <T> T readAndResetReadIndex(int readIndex, Function<BytesBuffer, T> function) {
        int index = readIndex();
        readIndex(readIndex);
        T apply = function.apply(this);
        readIndex(index);
        return apply;

    }


    default <T> T readAndResetReadIndex(Function<BytesBuffer, T> function) {
        return this.readAndResetReadIndex(readIndex(), function);
    }

    /**
     * write size    size = consumer write size + sizeLength
     * write consumer
     *
     * @param consumer
     */
    default void writeAllConsumerSize(Consumer<BytesBuffer> consumer, int sizeLength) {
        int sizeIndex = writeIndex();
        writeSizeResetWriteIndex(sizeIndex, 0, sizeLength);
        writeIndex(writeIndex() + sizeLength);
        int begin = writeIndex();
        consumer.accept(this);
        int end = writeIndex();
        writeSizeResetWriteIndex(sizeIndex, end - begin + sizeLength, sizeLength);
    }

    default byte[] readBytes(int length) {
        byte[] bytes = new byte[length];
        readBytes(bytes);
        return bytes;
    }

    default BytesBuffer sliceRead(int length) {
        return sliceIndex(readIndex(), length);
    }

    BytesBuffer sliceIndex(int index, int length);

    default int readableBytes() {
        return writeIndex() - readIndex();
    }
}
