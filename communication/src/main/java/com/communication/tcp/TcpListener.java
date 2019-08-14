package com.communication.tcp;

import io.netty.channel.Channel;

/**
 * Created by chenxiaojun on 2017/8/18.
 */

public interface TcpListener {
    void connect(Channel channel) throws Exception;

    void disConnect(Channel channel) throws Exception;

    void exception(Channel channel,Throwable cause) throws Exception;

    void receiveMsg(Channel channel,Object msg) throws Exception;
}
