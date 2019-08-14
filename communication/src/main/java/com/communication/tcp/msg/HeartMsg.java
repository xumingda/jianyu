package com.communication.tcp.msg;



/**
 * Created by chenxiaojun on 2017/8/16.
 */

public class HeartMsg extends BaseMsg {
    private static final long serialVersionUID = 9126644516468556157L;
    private static final String MsgBody="KeepConnect";
    public HeartMsg() {
        super(MsgType.HEART);
        setUid(-1);
        setMsgBody(MsgBody);
    }
    public HeartMsg(String msgBody) {
        super(MsgType.HEART);
        setUid(-1);
        setMsgBody(msgBody);
    }

}
