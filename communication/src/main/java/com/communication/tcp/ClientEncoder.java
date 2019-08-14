package com.communication.tcp;

import java.nio.CharBuffer;
import java.util.List;

import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;

/**
 * Created by chenxiaojun on 2017/8/16.
 * @Description:(编码类)
 *
 */
public class ClientEncoder extends StringEncoder {
    private static final String TAG = "Encoder";

    public ClientEncoder() {
        super(CharsetUtil.UTF_8);
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, CharSequence msg, List<Object> out) throws Exception {
        if (msg.length() == 0) {
            return;
        }
        out.add(ByteBufUtil.encodeString(ctx.alloc(), CharBuffer.wrap(msg), CharsetUtil.UTF_8));
    }
}