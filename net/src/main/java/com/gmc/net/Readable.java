package com.gmc.net;

public interface Readable {
    /**
     * Decode from bytesBuffer.
     * Only decode the fields in this implementation class.
     * This function don't involve bytesBuffer sizeLength.The sizeLength have processed in {@link MessageCodeManager}
     * Don't call {@link BytesBuffer#release()} in this function.The release function must be invoked in {@link MessageCodeManager}
     *
     * @param buffer
     */
    void read(BytesBuffer buffer);
}
