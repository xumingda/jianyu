package com.communication.tcp.msg;

import java.io.Serializable;

/**
 * Created by chenxiaojun on 2017/8/16.
 */

public abstract class BaseMsg implements Serializable {
    private static final long serialVersionUID = -2913778651728896967L;
    private MsgType mMsgType;
    private String mMsgBody;
    private int Uid;
    public BaseMsg(MsgType mMsgType){
        this.mMsgType=mMsgType;
    }

    public String getMsgBody() {
        return mMsgBody;
    }

    public void setMsgBody(String msgBody) {
        mMsgBody = msgBody;
    }

    public MsgType getMsgType() {
        return mMsgType;
    }

    public void setMsgType(MsgType msgType) {
        mMsgType = msgType;
    }

    public int getUid() {
        return Uid;
    }

    public void setUid(int uid) {
        Uid = uid;
    }

    @Override
    public String toString() {
        return "BaseMsg{" +
                "mMsgType=" + mMsgType +
                ", mMsgBody='" + mMsgBody + '\'' +
                ", Uid=" + Uid +
                '}';
    }
}
