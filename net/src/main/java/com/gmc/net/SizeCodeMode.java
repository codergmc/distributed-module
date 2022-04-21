package com.gmc.net;

/**
 * body length + body
 */
public class SizeCodeMode extends AbstractSizeCodeMode implements CodeMode {
    public static final CodeMode INSTANCE = new SizeCodeMode(4);

    public SizeCodeMode(int size) {
        super(size);
    }

    @Override
    public void encode(Writeable writeable, BytesBuffer buffer) {
        buffer.writeConsumerSize((ignore) -> writeable.write(buffer), sizeLength());
    }

    @Override
    public void decode(Readable readable, BytesBuffer buffer) {
        int size = readSize(buffer);
        BytesBuffer slice = buffer.sliceRead(size);
        readable.read(slice);
        buffer.readIndex(buffer.readIndex() + size);
    }

    @Override
    public BytesBuffer unwrap(BytesBuffer buffer) {
        return buffer.sliceIndex(sizeLength, buffer.readableBytes() - sizeLength);

    }

    @Override
    public int sizeLength() {
        return sizeLength;
    }
}
