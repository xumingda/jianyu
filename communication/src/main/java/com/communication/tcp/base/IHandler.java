package com.communication.tcp.base;

/**
 * Created by chenxiaojun on 2017/8/16.
 */

public interface IHandler {
    void connect(IChannel iChannel) throws Exception;

    void disConnect(IChannel iChannel) throws Exception;

    void exception(IChannel iChannel, Throwable cause) throws Exception;

    void receiveMsg(IChannel iChannel, Object msg) throws Exception;

}
