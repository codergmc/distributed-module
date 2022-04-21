package com.gmc.net;

public abstract class AbstractSizeCodeMode implements CodeMode {
    protected int sizeLength;

    public AbstractSizeCodeMode(int size) {
        this.sizeLength = size;
    }

    /**
     * sizeLength field length
     *
     * @return
     */
    abstract int sizeLength();

    @Override
    public int extraBytes() {
        return sizeLength();
    }

    int readSize(BytesBuffer buffer) {
        int length = sizeLength();
        if (length == 1) {
            return buffer.readByte();
        } else if (length == 2) {
            return buffer.readShort();
        } else if (length == 4) {
            return buffer.readInt();
        } else throw new IllegalArgumentException();
    }
}
