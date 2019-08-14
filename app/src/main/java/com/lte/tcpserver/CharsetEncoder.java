package com.lte.tcpserver;

import android.util.Log;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.apache.mina.filter.codec.textline.LineDelimiter;

import java.nio.charset.Charset;

/**
 * Created by chenxiaojun on 2017/8/28.
 */

public class CharsetEncoder implements ProtocolEncoder {
    private static String TAG = "CharsetEncoder";
    private final static Charset charset = Charset.forName("UTF-8");

    @Override
    public void dispose(IoSession session) throws Exception {
    }

    @Override
    public void encode(IoSession session, Object message, ProtocolEncoderOutput out) throws Exception {
        byte[] bytes = (byte[]) message;

        IoBuffer buffer = IoBuffer.allocate(1024);
        buffer.setAutoExpand(true);

        buffer.put(bytes);
        buffer.flip();

        out.write(buffer);
        out.flush();

        buffer.free();
    }
}
