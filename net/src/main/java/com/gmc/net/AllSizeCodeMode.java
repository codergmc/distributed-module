package com.gmc.net;

/**
 * body length +self size length  ---> body
 */
public class AllSizeCodeMode extends AbstractSizeCodeMode implements CodeMode {
    public static final CodeMode INSTANCE = new AllSizeCodeMode(4);

    public AllSizeCodeMode(int size) {
        super(size);
    }


    @Override
    public void encode(Writeable writeable, BytesBuffer buffer) {
        buffer.writeAllConsumerSize(ignore -> writeable.write(buffer), sizeLength);
    }

    @Override
    public void decode(Readable readable, BytesBuffer buffer) {

        int size = readSize(buffer);
        BytesBuffer slice = buffer.sliceRead(size - sizeLength());
        readable.read(slice);
        buffer.readIndex(buffer.readIndex() + size - sizeLength());

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
