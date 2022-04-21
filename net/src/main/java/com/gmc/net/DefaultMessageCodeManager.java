package com.gmc.net;

public class DefaultMessageCodeManager implements MessageCodeManager {
    protected final BytesBufferAllocate allocate;
    protected final MessageDistinguish messageDistinguish;
    protected final CodeMode all;
    protected final CodeMode head;
    protected final CodeMode body;
    protected final boolean releaseByteBufferForDecode;

    public DefaultMessageCodeManager(BytesBufferAllocate allocate, MessageDistinguish messageDistinguish, CodeMode all, CodeMode head, CodeMode body) {
        this(allocate, messageDistinguish, all, head, body, true);
    }

    public DefaultMessageCodeManager(BytesBufferAllocate allocate, MessageDistinguish messageDistinguish, CodeMode all, CodeMode head, CodeMode body, boolean releaseByteBufferForDecode) {
        this.allocate = allocate;
        this.messageDistinguish = messageDistinguish;
        this.all = all;
        this.head = head;
        this.body = body;
        this.releaseByteBufferForDecode = releaseByteBufferForDecode;
    }

    @Override
    public BytesBuffer encode(Message message) {
        message.setMessageCodeManager(this);
        BytesBuffer bytesBuffer;
        if (message instanceof FixedLength) {
            int size = ((FixedLength) message).size();
            bytesBuffer = allocate.allocateFixed(size + all.extraBytes() + head.extraBytes() + body.extraBytes());
        } else {
            bytesBuffer = allocate.allocateAutoIncr();
        }
        all.encode(message, bytesBuffer);
        return bytesBuffer;
    }

    @Override
    public void encodeHead(MessageHead message, BytesBuffer bytesBuffer) {
        head.encode(message, bytesBuffer);
    }

    @Override
    public void decodeHead(MessageHead message, BytesBuffer bytesBuffer) {
        head.decode(message, bytesBuffer);
    }

    @Override
    public void encodeBody(MessageBody message, BytesBuffer bytesBuffer) {
        body.encode(message, bytesBuffer);
    }

    @Override
    public void decodeBody(MessageBody message, BytesBuffer buffer) {
        body.decode(message, buffer);
    }

    @Override
    public Message decode(BytesBuffer bytesBuffer) {
        Message message = messageDistinguish(bytesBuffer);
        all.decode(message, bytesBuffer);
        if (releaseByteBufferForDecode) {
            bytesBuffer.release();
        }
        return message;
    }

    @Override
    public Message messageDistinguish(BytesBuffer bytesBuffer) {
        BytesBuffer unwrap = head.unwrap(all.unwrap(bytesBuffer));
        Message message = unwrap.readAndResetReadIndex(messageDistinguish::distinguish);
        message.setMessageCodeManager(this);
        return message;
    }

    @Override
    public CodeMode allCodeMode() {
        return all;
    }

    @Override
    public CodeMode headCodeMode() {
        return head;
    }

    @Override
    public CodeMode bodyCodeMode() {
        return body;
    }

    public BytesBufferAllocate getAllocate() {
        return allocate;
    }

    public MessageDistinguish getMessageDistinguish() {
        return messageDistinguish;
    }
}
