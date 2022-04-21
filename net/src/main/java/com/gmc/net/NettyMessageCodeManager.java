package com.gmc.net;

import io.netty.channel.Channel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

public class NettyMessageCodeManager extends DefaultMessageCodeManager implements NettyInit {
    public NettyMessageCodeManager(BytesBufferAllocate allocate, MessageDistinguish messageDistinguish, CodeMode all, CodeMode head, CodeMode body) {
        super(allocate, messageDistinguish, all, head, body);
    }

    public NettyMessageCodeManager(BytesBufferAllocate allocate, MessageDistinguish messageDistinguish, CodeMode all, CodeMode head, CodeMode body, boolean releaseByteBufferForDecode) {
        super(allocate, messageDistinguish, all, head, body, releaseByteBufferForDecode);
    }

    @Override
    public void nettyInit(Channel channel) {
        LengthFieldBasedFrameDecoder fieldBasedFrameDecoder = NettyUtils.getFrameDecoder(this);
        if (fieldBasedFrameDecoder != null) {
            channel.pipeline().addLast(fieldBasedFrameDecoder);
        }
        channel.pipeline().addLast(NettyUtils.getBytesBufferWrapHandler());
        channel.pipeline().addLast(NettyUtils.getNettyCodeHandler(this));
    }
}
