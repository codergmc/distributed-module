package com.gmc.net;

import io.netty.buffer.ByteBuf;


public class NettyBytesBuffer extends AbstractBytesBuffer {
    protected ByteBuf byteBuf;

    public NettyBytesBuffer(ByteBuf byteBuf) {
        this.byteBuf = byteBuf;
    }


    @Override
    public void writeChar(char value) {
        byteBuf.writeChar(value);

    }

    @Override
    public void writeLong(long value) {
        byteBuf.writeLong(value);

    }

    @Override
    public void writeDouble(double value) {
        byteBuf.writeDouble(value);

    }

    @Override
    public void writeShort(short value) {
        byteBuf.writeShort(value);
    }

    @Override
    public void writeInt(int value) {
        byteBuf.writeInt(value);
    }

    @Override
    public void writeByte(byte value) {
        byteBuf.writeByte(value);
    }


    @Override
    public void writeBytes(byte[] bytes) {
        byteBuf.writeBytes(bytes);
    }

    @Override
    public void writeFloat(float value) {
        byteBuf.writeFloat(value);
    }

    @Override
    public void readBytes(byte[] bytes) {
        byteBuf.readBytes(bytes);
    }


    @Override
    public int readInt() {
        return byteBuf.readInt();
    }

    @Override
    public short readShort() {
        return byteBuf.readShort();
    }

    @Override
    public byte readByte() {
        return byteBuf.readByte();
    }

    @Override
    public char readChar() {
        return byteBuf.readChar();
    }

    @Override
    public long readLong() {
        return byteBuf.readLong();
    }

    @Override
    public double readDouble() {
        return byteBuf.readDouble();
    }

    @Override
    public float readFloat() {
        return byteBuf.readFloat();
    }

    @Override
    public int readIndex() {
        return byteBuf.readerIndex();
    }

    public void readIndex(int readIndex) {
        byteBuf.readerIndex(readIndex);
    }

    @Override
    public int writeIndex() {
        return byteBuf.writerIndex();
    }

    @Override
    public void writeIndex(int writeIndex) {
        byteBuf.writerIndex(writeIndex);
    }

    @Override
    public int actualSize() {
        return byteBuf.maxFastWritableBytes() + byteBuf.writerIndex();
    }


    @Override
    public int requireSize() {
        return byteBuf.capacity();
    }

    @Override
    public void release() {
        byteBuf.release();
    }

    @Override
    public void markReadIndex() {
        byteBuf.markReaderIndex();
    }

    @Override
    public void markWriteIndex() {
        byteBuf.markWriterIndex();

    }

    public static BytesBuffer wrap(ByteBuf byteBuf) {
        return new NettyBytesBuffer(byteBuf);

    }

    @Override
    public void resetReadIndex() {
        byteBuf.resetReaderIndex();
    }

    @Override
    public void resetWriteIndex() {
        byteBuf.resetWriterIndex();
    }

    @Override
    public void discardReadBytes() {
        byteBuf.discardReadBytes();
    }

    @Override
    public BytesBuffer sliceIndex(int index, int length) {
        return new NettyBytesBuffer(byteBuf.slice(index, length));
    }

    public ByteBuf getInnerByteBuf() {
        ByteBuf byteBuf = this.byteBuf;
        return byteBuf;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NettyBytesBuffer that = (NettyBytesBuffer) o;

        return byteBuf != null ? byteBuf.equals(that.byteBuf) : that.byteBuf == null;
    }

    @Override
    public int hashCode() {
        return byteBuf != null ? byteBuf.hashCode() : 0;
    }
}
