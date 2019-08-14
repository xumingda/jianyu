package com.communication.tcp;

import android.text.TextUtils;
import android.util.Log;

import com.communication.tcp.msg.BaseMsg;
import com.communication.tcp.msg.HeartMsg;
import com.communication.tcp.msg.ReceiveMsg;
import com.communication.utils.LETLog;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

/**
 * Created by chenxiaojun on 2017/8/16.
 *
 * @Description:(解码类，对收到消息解码)
 */
public class ClientDecoder extends ByteToMessageDecoder {
    private String TAG = "Decoder";

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in,
                          List<Object> out) throws Exception {
        LETLog.d("decode");
        in.resetReaderIndex();
        ByteBuf str_buf = in.readBytes(in.readableBytes());
        byte[] decoded = new byte[str_buf.readableBytes()];
        str_buf.readBytes(decoded);
        String str = new String(decoded);
        out.add(parseMessage(str));
        in.discardReadBytes();
    }

    /**
     * @param data 数据
     * @return BaseMsg
     * @throws
     * @Title: parseMessage
     * @Description: TODO(消息转换)
     */
    private BaseMsg parseMessage(String data) {
//        if (TextUtils.isEmpty(data)) {
//            return null;
//        }
//        if (data.contains("KeepConnect")) {
//            return new HeartMsg();
//        }else {
            return new ReceiveMsg(-1,data);
//        }
    }
}
