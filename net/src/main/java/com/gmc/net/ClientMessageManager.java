package com.gmc.net;

import java.io.Closeable;

public interface ClientMessageManager extends LifeCycle {
    /**
     * @param message
     * @param sendNotify
     * @param receiveNotify
     * @return messageId
     */
    long putMessage(Message message, Client.SendNotify sendNotify, Client.ReceiveNotify receiveNotify);

    void receiveMessage(Message message);

    void start();

    class MessageWrap {
        private final Message message;
        private final Client.SendNotify sendNotify;
        private final Client.ReceiveNotify receiveNotify;
        private boolean processed = false;
        private int retry;

        public MessageWrap(Message message, Client.SendNotify sendNotify, Client.ReceiveNotify receiveNotify, int retry) {
            this.message = message;
            this.sendNotify = sendNotify;
            this.receiveNotify = receiveNotify;
            this.retry = retry;

        }

        public boolean retry() {
            return retry > 0;
        }

        public void decreaseRetry() {
            retry--;
        }

        public Message getMessage() {
            return message;
        }

        public Client.SendNotify getSendNotify() {
            return sendNotify;
        }

        public void markProcessed() {
            processed = true;
        }

        public boolean hasProcessed() {
            return processed;
        }

        public Client.ReceiveNotify getReceiveNotify() {
            return receiveNotify;
        }
    }
}
