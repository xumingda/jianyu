package com.communication.tcp.msg;

/**
 * Created by chenxiaojun on 2017/8/16.
 */

public class ReceiveMsg extends BaseMsg {
    private static final long serialVersionUID = 2758594125223084198L;
    public ReceiveMsg(){
        super(MsgType.RECEIVE);
    }
    public ReceiveMsg(int id,String body){
        super(MsgType.RECEIVE);
        setMsgBody(body);
        setUid(id);
    }
}
