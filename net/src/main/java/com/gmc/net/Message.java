package com.gmc.net;

public class Message implements Readable, Writeable {
    protected MessageCodeManager messageCodeManager;
    protected MessageHead head;
    protected MessageBody body;

    public Message(MessageHead head, MessageBody body) {
        this.head = head;
        this.body = body;
    }


    public MessageHead getHead() {
        return head;
    }

    public Message setHead(MessageHead head) {
        this.head = head;
        return this;
    }

    public MessageBody getBody() {
        return body;
    }

    public Message setBody(MessageBody body) {
        this.body = body;
        return this;
    }

    @Override
    public void read(BytesBuffer bytesBuffer) {
        messageCodeManager.decodeHead(head, bytesBuffer);
        messageCodeManager.decodeBody(body, bytesBuffer);
    }

    @Override
    public void write(BytesBuffer buffer) {
        messageCodeManager.encodeHead(head, buffer);
        messageCodeManager.encodeBody(body, buffer);
    }

    public Message setMessageCodeManager(MessageCodeManager messageCodeManager) {
        this.messageCodeManager = messageCodeManager;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Message message = (Message) o;

        if (head != null ? !head.equals(message.head) : message.head != null) return false;
        return body != null ? body.equals(message.body) : message.body == null;
    }

    @Override
    public int hashCode() {
        int result = head != null ? head.hashCode() : 0;
        result = 31 * result + (body != null ? body.hashCode() : 0);
        return result;
    }
}
