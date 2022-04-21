package com.gmc.net;

public abstract class AbstractAutoIncrBytesBuffer implements BytesBuffer {
    protected BytesBufferAllocate bytesBufferAllocate;
    protected BytesBuffer bytesBuffer;

    public AbstractAutoIncrBytesBuffer(BytesBufferAllocate bytesBufferAllocate, BytesBuffer bytesBuffer) {
        this.bytesBufferAllocate = bytesBufferAllocate;
        this.bytesBuffer = bytesBuffer;
    }


    @Override
    public void writeInt(int value) {
        ensureEnoughSpace(bytesBuffer.actualSize() + 4);
        bytesBuffer.writeInt(value);
    }

    protected abstract void ensureEnoughSpace(int capacity);


    @Override
    public void writeShort(short value) {
        ensureEnoughSpace(bytesBuffer.actualSize() + 2);

        bytesBuffer.writeShort(value);

    }

    @Override
    public void writeByte(byte value) {
        ensureEnoughSpace(bytesBuffer.actualSize() + 1);

        bytesBuffer.writeByte(value);

    }

    @Override
    public void discardReadBytes() {
        bytesBuffer.discardReadBytes();
    }

    @Override
    public void writeChar(char value) {
        ensureEnoughSpace(bytesBuffer.actualSize() + 2);

        bytesBuffer.writeChar(value);

    }

    @Override
    public void writeLong(long value) {
        ensureEnoughSpace(bytesBuffer.actualSize() + 8);
        bytesBuffer.writeLong(value);

    }

    @Override
    public void writeDouble(double value) {
        ensureEnoughSpace(bytesBuffer.actualSize() + 8);
        bytesBuffer.writeDouble(value);

    }

    @Override
    public void writeFloat(float value) {
        ensureEnoughSpace(bytesBuffer.actualSize() + 4);
        bytesBuffer.writeFloat(value);

    }

    @Override
    public void writeBytes(byte[] bytes) {
        ensureEnoughSpace(bytesBuffer.actualSize() + bytes.length);
        bytesBuffer.writeBytes(bytes);
    }

    @Override
    public void readBytes(byte[] bytes) {
        bytesBuffer.readBytes(bytes);
    }

    @Override
    public int readInt() {
        return bytesBuffer.readInt();
    }

    @Override
    public short readShort() {
        return bytesBuffer.readShort();
    }

    @Override
    public byte readByte() {
        return bytesBuffer.readByte();
    }

    @Override
    public char readChar() {
        return bytesBuffer.readChar();
    }

    @Override
    public long readLong() {
        return bytesBuffer.readLong();
    }

    @Override
    public double readDouble() {
        return bytesBuffer.readDouble();
    }

    @Override
    public float readFloat() {
        return bytesBuffer.readFloat();
    }

    @Override
    public int readIndex() {
        return bytesBuffer.readIndex();
    }

    @Override
    public void readIndex(int readIndex) {
        bytesBuffer.readIndex(readIndex);
    }

    @Override
    public int writeIndex() {
        return bytesBuffer.writeIndex();
    }

    @Override
    public void writeIndex(int writeIndex) {
        bytesBuffer.writeIndex(writeIndex);
    }

    @Override
    public int actualSize() {
        return bytesBuffer.actualSize();
    }

    @Override
    public int requireSize() {
        return bytesBuffer.requireSize();
    }

    @Override
    public void release() {
        bytesBuffer.release();
        bytesBuffer = null;
    }

    @Override
    public void markReadIndex() {
        bytesBuffer.markReadIndex();
    }

    @Override
    public void markWriteIndex() {
        bytesBuffer.markWriteIndex();
    }

    @Override
    public void resetReadIndex() {
        bytesBuffer.resetReadIndex();
    }

    @Override
    public void resetWriteIndex() {
        bytesBuffer.resetWriteIndex();
    }

    @Override
    public BytesBuffer sliceIndex(int index, int length) {
        return bytesBuffer.sliceIndex(index, length);
    }

}
