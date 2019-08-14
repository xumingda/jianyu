package com.communication.tcp.base;


/**
 * Created by chenxiaojun on 2017/8/16.
 */

public interface IMessage {
    void receiveMsg(int uid, String data);

    void onFailed(Exception e);
}
