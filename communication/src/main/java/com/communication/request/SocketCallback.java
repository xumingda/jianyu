package com.communication.request;

/**
 * Created by chenxiaojun on 2017/8/16.
 */

public interface SocketCallback {
    void onSuccess(String jsonObject);
    void onFailed(Exception errorMsg);
}
