package com.gmc.net;

/**
 * no sizeLength
 */
public class DefaultCodeMode implements CodeMode {
    public static final CodeMode INSTANCE = new DefaultCodeMode();

    @Override
    public void encode(Writeable writeable, BytesBuffer buffer) {
        writeable.write(buffer);
    }

    @Override
    public void decode(Readable readable, BytesBuffer buffer) {
        readable.read(buffer);

    }

    @Override
    public BytesBuffer unwrap(BytesBuffer buffer) {
        return buffer.sliceRead(buffer.readableBytes());
    }

    @Override
    public int extraBytes() {
        return 0;
    }
}
