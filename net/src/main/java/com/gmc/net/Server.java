package com.gmc.net;

import java.net.InetSocketAddress;

public interface Server {
    void start() throws InterruptedException;

    void close() throws InterruptedException;

    interface Channel {
        /**
         * @param message
         * @return future is success if message was flush
         */
        CommonFuture<?> write(Message message);

        void flush();

        void writeAndFlush(Message message);
        CommonFuture<?> close();
    }
}
