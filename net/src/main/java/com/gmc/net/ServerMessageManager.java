package com.gmc.net;

public interface ServerMessageManager {
    void receiveMessage(MessageWrap messageWrap);

    void channelClose(Server.Channel channel);

    void channelActive(Server.Channel channel);

    class MessageWrap<T extends Message> {
        T message;
        Server.Channel channel;

        public MessageWrap(T message, Server.Channel channel) {
            this.message = message;
            this.channel = channel;
        }

        public T getMessage() {
            return message;
        }

        public Server.Channel getChannel() {
            return channel;
        }
    }


}
