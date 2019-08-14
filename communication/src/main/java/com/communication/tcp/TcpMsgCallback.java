package com.communication.tcp;

/**
 * Created by chenxiaojun on 2017/8/18.
 */

public interface TcpMsgCallback {
    void receiveMsg( String data);

    void onFailed(Exception e);
}
