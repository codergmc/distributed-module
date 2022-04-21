package com.gmc.net;

import java.io.Closeable;
import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;

public interface ClientConnectManager extends LifeCycle {
    CompletableFuture<Void> connect(InetSocketAddress remote);

    void addConnectListener(Client.Channel.Listener listener);


}
