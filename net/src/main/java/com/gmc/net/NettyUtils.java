package com.gmc.net;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import java.util.concurrent.CancellationException;
import java.util.concurrent.TimeUnit;

public class NettyUtils {
    public static LengthFieldBasedFrameDecoder getFrameDecoder(MessageCodeManager messageCodeManager) {
        CodeMode codeMode = messageCodeManager.allCodeMode();

        if (codeMode instanceof AllSizeCodeMode) {
            return new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, ((AllSizeCodeMode) codeMode).sizeLength(), -((AllSizeCodeMode) codeMode).sizeLength(), 0);

        } else if (codeMode instanceof SizeCodeMode) {
            return new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, ((SizeCodeMode) codeMode).sizeLength(), 0, 0);

        } else if (codeMode instanceof DefaultCodeMode) {
            return new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 0, 0, 0);

        } else throw new IllegalArgumentException();

    }

    public static ChannelDuplexHandler getBytesBufferWrapHandler() {
        return new BytesBufferWrapHandler();
    }

    public static ChannelDuplexHandler getNettyCodeHandler(MessageCodeManager messageCodeManager) {
        return new NettyCodeHandler(messageCodeManager);
    }
    public static ChannelInboundHandlerAdapter getNettyClientResetHeartbeatHandler(ClientHeartbeatManager clientHeartbeatManager){
        return new ClientResetHeartbeatHandler(clientHeartbeatManager);
    }
    public static ChannelDuplexHandler getNettyMessageHandler(ServerMessageManager messageManager) {
        return new ServerMessageManagerHandler(messageManager);
    }

    public static ChannelDuplexHandler getNettyClientMessageHandler(ClientMessageManager clientMessageManager) {
        return new ClientMessageManagerHandler(clientMessageManager);
    }
    public static ChannelDuplexHandler getNettyClientHeartbeat(ClientHeartbeatManager heartbeatManager,long readTimeout,TimeUnit timeUnit){
        return new ClientHeartbeatHandler(heartbeatManager,readTimeout,timeUnit);
    }

    public static ChannelDuplexHandler getNettyExceptionHandler() {
        return new ExceptionHandler();
    }

    private static class ExceptionHandler extends ChannelDuplexHandler {

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            ctx.pipeline().close();
        }
    }

    private static class ClientMessageManagerHandler extends ChannelDuplexHandler {
        ClientMessageManager clientMessageManager;

        public ClientMessageManagerHandler(ClientMessageManager clientMessageManager) {
            this.clientMessageManager = clientMessageManager;
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            clientMessageManager.receiveMessage((Message) msg);
            super.channelRead(ctx, msg);
        }
    }

    private static class ServerMessageManagerHandler extends ChannelDuplexHandler {
        ServerMessageManager serverMessageManager;
        AttributeKey<Server.Channel> attributeKey = AttributeKey.valueOf("channel");

        public ServerMessageManagerHandler(ServerMessageManager serverMessageManager) {
            this.serverMessageManager = serverMessageManager;
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            Attribute<Server.Channel> attr = ctx.channel().attr(attributeKey);
            NettyChannel nettyChannel = new NettyChannel(ctx.channel());
            attr.set(nettyChannel);
            serverMessageManager.channelActive(nettyChannel);
            super.channelActive(ctx);
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            Attribute<Server.Channel> attr = ctx.channel().attr(attributeKey);
            serverMessageManager.channelClose(attr.get());
            attr.set(null);
            super.channelInactive(ctx);
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            Server.Channel channel = ctx.channel().attr(attributeKey).get();
            serverMessageManager.receiveMessage(new ServerMessageManager.MessageWrap(((Message) msg), channel));
            super.channelRead(ctx, msg);
        }

    }

    private static class NettyCodeHandler extends ChannelDuplexHandler {
        MessageCodeManager messageCodeManager;

        public NettyCodeHandler(MessageCodeManager messageCodeManager) {
            this.messageCodeManager = messageCodeManager;
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            BytesBuffer bytesBuffer = (BytesBuffer) msg;
            super.channelRead(ctx, messageCodeManager.decode(bytesBuffer));
        }

        @Override
        public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
            Message message = (Message) msg;
            super.write(ctx, messageCodeManager.encode(message), promise);
        }
    }

    private static class NettyChannel implements Server.Channel {
        io.netty.channel.Channel channel;

        public NettyChannel(Channel channel) {
            this.channel = channel;
        }

        @Override
        public CommonFuture<?> write(Message message) {
            CommonFuture<?> result = CommonFuture.createFuture();
            channel.write(message).addListener(future -> {
                if (future.isSuccess()) {
                    result.completeValue(null);

                } else {
                    result.completeException(future.cause());
                }
            });
            return result;
        }

        @Override
        public void flush() {
            channel.flush();
        }

        @Override
        public void writeAndFlush(Message message) {
            channel.writeAndFlush(message);
        }

        @Override
        public CommonFuture<?> close() {
            CommonFuture<?> commonFuture = new DefaultCommonFuture<>();
            channel.close().addListener(future -> {
                if (future.isSuccess()) {
                    commonFuture.completeValue(null);
                } else {
                    if (future.isCancelled()) {
                        commonFuture.completeException(new CancellationException());
                    } else {
                        commonFuture.completeException(future.cause());
                    }
                }
            });
            return commonFuture;
        }
    }

    static class ClientHeartbeatHandler extends IdleStateHandler {
        ClientHeartbeatManager clientHeartbeatManager;

        public ClientHeartbeatHandler(ClientHeartbeatManager clientHeartbeatManager, long readerIdleTime, TimeUnit unit) {
            super(readerIdleTime, 0, 0, unit);
            this.clientHeartbeatManager = clientHeartbeatManager;
        }

        @Override
        protected void channelIdle(ChannelHandlerContext ctx, IdleStateEvent evt) throws Exception {
            if (evt.state() == IdleState.READER_IDLE) {
                clientHeartbeatManager.sendHeartbeat();
            }
            if (evt.state() == IdleState.WRITER_IDLE) {

            }
            super.channelIdle(ctx, evt);
        }
    }
    private static class ClientResetHeartbeatHandler extends ChannelInboundHandlerAdapter {
        ClientHeartbeatManager clientHeartbeatManager;

        public ClientResetHeartbeatHandler(ClientHeartbeatManager clientHeartbeatManager) {
            this.clientHeartbeatManager = clientHeartbeatManager;
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            clientHeartbeatManager.resetHeartbeat();
            super.channelRead(ctx, msg);
        }
    }
    private static class BytesBufferWrapHandler extends ChannelDuplexHandler {
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            ByteBuf byteBuf = (ByteBuf) msg;
            super.channelRead(ctx, NettyBytesBuffer.wrap(byteBuf));
        }

        @Override
        public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
            NettyBytesBuffer bytesBuffer = (NettyBytesBuffer) msg;
            super.write(ctx, bytesBuffer.getInnerByteBuf(), promise);
        }
    }
}
