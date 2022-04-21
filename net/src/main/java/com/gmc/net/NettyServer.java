package com.gmc.net;

import com.gmc.config.ConfigProperty;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

import java.net.InetSocketAddress;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class NettyServer extends AbstractServer {
    ServerBootstrap serverBootstrap = new ServerBootstrap();
    EventLoopGroup mainLoop;
    EventLoopGroup childLoop;
    ChannelFuture channelFuture;

    public NettyServer(ConfigProperty serverConfig, MessageCodeManager messageCodeManager, ServerMessageManager serverMessageManager) {
        super(messageCodeManager, serverMessageManager, serverConfig);
        mainLoop = new NioEventLoopGroup(1, new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "main");
            }
        });
        if (serverConfig.getConfig(ServerConfig.WORK_THREAD_NUM) > 0) {
            childLoop = new NioEventLoopGroup(serverConfig.getConfig(ServerConfig.WORK_THREAD_NUM), new ThreadFactory() {
                AtomicInteger index = new AtomicInteger();

                @Override
                public Thread newThread(Runnable r) {
                    return new Thread(r, "work-" + index.incrementAndGet());
                }
            });
        }
        serverBootstrap.channel(NioServerSocketChannel.class);
        serverBootstrap.childOption(ChannelOption.TCP_NODELAY, true);
        serverBootstrap.childOption(ChannelOption.SO_LINGER, 0);
        if (childLoop != null) {
            serverBootstrap.group(mainLoop, childLoop);
        } else serverBootstrap.group(mainLoop);
        serverBootstrap.childHandler(new ChannelInitializer<>() {

            @Override
            protected void initChannel(io.netty.channel.Channel ch) {
                LengthFieldBasedFrameDecoder fieldBasedFrameDecoder = NettyUtils.getFrameDecoder(messageCodeManager);
                if (fieldBasedFrameDecoder != null) {
                    ch.pipeline().addLast(fieldBasedFrameDecoder);
                }
                ch.pipeline().addLast(NettyUtils.getBytesBufferWrapHandler());
                ch.pipeline().addLast(NettyUtils.getNettyCodeHandler(messageCodeManager));
                ch.pipeline().addLast(NettyUtils.getNettyMessageHandler(serverMessageManager));
                ch.pipeline().addLast(NettyUtils.getNettyExceptionHandler());
            }
        });

    }


    @Override
    public void start() throws InterruptedException {
        channelFuture = serverBootstrap.bind(host, port).sync();

    }

    @Override
    public void close() throws InterruptedException {
        channelFuture.channel().close().sync();
        mainLoop.shutdownGracefully();
        childLoop.shutdownGracefully();
    }


}
