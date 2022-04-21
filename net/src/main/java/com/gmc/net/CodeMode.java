package com.gmc.net;

public interface CodeMode {
    void encode(Writeable writeable, BytesBuffer buffer);

    void decode(Readable readable, BytesBuffer buffer);

    /**
     * return new bytesBuffer only take out bytes that used separate message
     *
     * @param buffer
     * @return
     */
    BytesBuffer unwrap(BytesBuffer buffer);

    /**
     *
     * @return extra bytes that code
     */
    int extraBytes();


}
