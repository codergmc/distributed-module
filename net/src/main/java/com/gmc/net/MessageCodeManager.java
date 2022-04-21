package com.gmc.net;

public interface MessageCodeManager {
    /**
     * encode message.
     * decision the message structure.
     * eg. message sizeLength(4 bytes) |  message head  | message body
     *
     * @param message
     * @return
     */
    BytesBuffer encode(Message message);

    /**
     * encode head
     *
     * @param message
     * @param bytesBuffer
     */
    void encodeHead(MessageHead message, BytesBuffer bytesBuffer);

    void decodeHead(MessageHead message, BytesBuffer bytesBuffer);

    /**
     * @param message
     * @param buffer
     */
    void encodeBody(MessageBody message, BytesBuffer buffer);

    void decodeBody(MessageBody message, BytesBuffer buffer);

    Message decode(BytesBuffer bytesBuffer);

    /**
     * can not modify bytesBuffer state(eg. readIndex,writeIndex ).If you must modify,please reset state in the end of function
     *
     * @param bytesBuffer
     * @return
     */
    Message messageDistinguish(BytesBuffer bytesBuffer);

    CodeMode allCodeMode();

    CodeMode headCodeMode();

    CodeMode bodyCodeMode();

}
