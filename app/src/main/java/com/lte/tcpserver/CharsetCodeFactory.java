package com.lte.tcpserver;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;



/**
 * Created by chenxiaojun on 2017/8/28.
 */

public class CharsetCodeFactory implements ProtocolCodecFactory {

    @Override
    public ProtocolDecoder getDecoder(IoSession session) throws Exception {//返回一个解码器
        return new CharsetDecoder();
    }

    @Override
    public ProtocolEncoder getEncoder(IoSession session) throws Exception {//返回一个编码器
        return new CharsetEncoder();
    }
}
