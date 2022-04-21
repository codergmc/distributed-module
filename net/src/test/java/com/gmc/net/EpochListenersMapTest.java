package com.gmc.net;

import com.gmc.core.LogUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.Timeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.stream.FactoryConfigurationError;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EpochListenersMapTest {
    static ExecutorService executorService;

    @BeforeAll
    static void init() {
        executorService = Executors.newFixedThreadPool(4);
    }
    @RepeatedTest(2)
    @Timeout(10)
    @Test
    void test() throws ExecutionException, InterruptedException {
        NettyClientChannel nettyClientChannel = new NettyClientChannel();
        NettyClientChannel.EpochListenersMap epochListeners = nettyClientChannel.new EpochListenersMap();
        int listenerSize = 20;
        int loopSize = 2;
        int maxEpoch = 0;
        List<CompletableFuture> list = new ArrayList<>();
        while (loopSize-- > 0) {
            for (int i = 0; i < listenerSize; i++) {
                CompletableFuture completableFuture = new CompletableFuture();
                list.add(completableFuture);
                int finalI = i;
                int finalMaxEpoch = maxEpoch;
                int finalLoopSize = loopSize;
                executorService.execute(new Runnable() {
                    @Override
                    public void run() {
                        int epoch = new Random().nextInt(finalMaxEpoch + 1);
                        epochListeners.addListener(epoch, new TestListener(completableFuture, finalI, finalLoopSize, epoch), false);
                    }
                });

            }
            epochListeners.notifyListener(maxEpoch, false);
            maxEpoch++;
        }
        for (int i = 0; i < list.size(); i++) {
            list.get(i).get();
            System.out.println(i);
        }


    }

    class TestListener implements Client.Channel.Listener {
        CompletableFuture<Void> completableFuture;
        int index;
        int epoch;
        int loopIndex;

        public TestListener(CompletableFuture<Void> completableFuture, int index, int loopIndex, int epoch) {
            this.completableFuture = completableFuture;
            this.index = index;
            this.epoch = epoch;
            this.loopIndex = loopIndex;
        }

        @Override
        public void notify(Client.Channel channel, int epoch) {
            completableFuture.complete(null);
        }

        @Override
        public String toString() {
            return "TestListener{" +
                    "index=" + index +
                    ", epoch=" + epoch +
                    ", loopIndex=" + loopIndex +
                    '}';
        }
    }
}
