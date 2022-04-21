package com.gmc.net;

import com.gmc.config.ConfigProperty;
import com.gmc.core.LogUtils;
import com.gmc.core.Tuple2;

import java.io.Closeable;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public interface Client extends LifeCycle {

    default void send(Writeable writeable) {
        send(writeable, SendNotify.DO_NOTHING);
    }

    CloseableScheduledExecutor getExecutor();

    /**
     * connect remote address.This function is not sync,so it maybe not connect after invoke this function
     *
     * @param address
     */


    CompletableFuture<Client> connectAsync(InetSocketAddress address);

    ConfigProperty getConfig();

    Channel getChannel();

    ClientHeartbeatManager getClientHeartbeatManager();

    ClientConnectManager getClientConnectManager();

    ClientMessageManager getClientMessageManager();

    MessageCodeManager getMessageCodeManager();

    default void connectSync(InetSocketAddress address) throws InterruptedException, ExecutionException {
        CompletableFuture<Client> clientFuture = connectAsync(address);
        clientFuture.get();
    }

    default void send(Writeable writeable, SendNotify sendNotify) {
        send(writeable, sendNotify, ReceiveNotify.DO_NOTHING);
    }


    void send(Writeable writeable, SendNotify sendNotify, ReceiveNotify receiveNotify);

    interface SendNotify {
        SendNotify DO_NOTHING = new SendNotify() {
            @Override
            public void hasSent() {

            }

            @Override
            public void onException(Throwable throwable) {

            }
        };

        /**
         * invoke when the writeable have sent
         */

        void hasSent();

        void onException(Throwable throwable);
    }

    interface ReceiveNotify {
        ReceiveNotify DO_NOTHING = new ReceiveNotify() {
            @Override
            public void receive(Readable readable) {

            }

            @Override
            public void onException(Throwable throwable) {

            }
        };

        void receive(Readable readable);

        void onException(Throwable throwable);
    }

    interface ConnectListener {
        ConnectListener DO_NOTHING = new ConnectListener() {
            @Override
            public void onConnect(Client client) {

            }

            @Override
            public void onException(Throwable throwable) {

            }

        };

        void onConnect(Client client);

        void onException(Throwable throwable);
    }

    interface Channel extends Closeable {
        void init();

        CompletableFuture<Void> write(Message message);

        /**
         * if this state is equals {@link State#DISCONNECT} and some thread invoke {@code connect} method,
         * only one thread will {@code connect}  invoke method and other thread do nothing
         * <p>
         * if this thread doing connect,return the  {@code CompletableFuture} represent the connect result.
         * if other thread doing connect, return the  {@code CompletableFuture} that complete when other thread connect success,
         * but if  other thread connect fail ,the  {@link CompletableFuture#isDone()} will return false;
         *
         * @param address
         * @param oldEpoch
         * @return
         */
        CompletableFuture<Tuple2<Integer, Channel>> connect(InetSocketAddress address, int oldEpoch);

        void flush();

        void close();

        boolean isConnected();

        /**
         * when the connector that epoch equals param {@code epoch} disconnect ,the listener will be notify
         *
         * @param listener
         * @param epoch
         */
        void addDisConnectListener(Listener listener, int epoch);

        /**
         * add connect listener
         * when the new connector that the epoch = {@code oldEpoch} +1  established ,the listener will be notify
         *
         * @param listener
         * @param oldEpoch
         */
        void addConnectListener(Listener listener, int oldEpoch);

        int getEpoch();

        enum State {
            NEW(0), INIT(1), CONNECTING(2), DISCONNECT(3), CONNECTED(4), CLOSE(5);
            int state;
            //old state -> new state
            static Map<State, Set<State>> CHANGE_MAP = new HashMap<>();

            static {
                CHANGE_MAP.computeIfAbsent(NEW, key -> new HashSet<>()).add(INIT);

                CHANGE_MAP.computeIfAbsent(INIT, key -> new HashSet<>()).add(CONNECTING);

                CHANGE_MAP.computeIfAbsent(CONNECTING, key -> new HashSet<>()).add(DISCONNECT);
                CHANGE_MAP.computeIfAbsent(CONNECTING, key -> new HashSet<>()).add(CONNECTED);
                CHANGE_MAP.computeIfAbsent(CONNECTING, key -> new HashSet<>()).add(CLOSE);
                CHANGE_MAP.computeIfAbsent(DISCONNECT, key -> new HashSet<>()).add(CONNECTING);
                CHANGE_MAP.computeIfAbsent(DISCONNECT, key -> new HashSet<>()).add(CLOSE);
                CHANGE_MAP.computeIfAbsent(CONNECTED, key -> new HashSet<>()).add(DISCONNECT);
                CHANGE_MAP.computeIfAbsent(CONNECTED, key -> new HashSet<>()).add(CLOSE);

            }

            State(int state) {
                this.state = state;
            }

            public boolean canTransform(State state) throws ClientStateTransformException, ClientSameStateException {
                if (this.equals(state)) {
                    throw new ClientSameStateException(this, state);
                }
                if (!CHANGE_MAP.get(this).contains(state)) {
                    throw new ClientStateTransformException(this, state);
                }
                return true;
            }
        }

        class ClientSameStateException extends Exception {
            public ClientSameStateException(State oriState, State Transform) {
                super(LogUtils.format("same state can not transform, ori state:{},transform state:{}", oriState.name(), Transform.name()));
            }
        }

        class ClientStateTransformException extends Exception {
            public ClientStateTransformException(State oriState, State Transform) {
                super(LogUtils.format("can not transform state, ori state:{},transform state:{}", oriState.name(), Transform.name()));
            }
        }

        class UseOldEpochException extends Exception {
            public UseOldEpochException(int oldEpoch, int curEpoch) {
                super(LogUtils.format("use old epoch, old epoch:{},cur epoch:{}", oldEpoch, curEpoch));
            }
        }

        /**
         * subclass must extend AbstractListener to  prevent invoke notify more than once
         */
        interface Listener {

            void notify(Channel channel, int epoch);
        }

        abstract class AbstractListener implements Listener {
            AtomicBoolean notify = new AtomicBoolean(false);

            @Override
            public void notify(Channel channel, int epoch) {
                if (notify.compareAndSet(false, true)) {
                    notify0(channel,epoch);
                } else throw new RuntimeException();
            }

            public abstract void notify0(Channel channel, int epoch);
        }

    }

}
