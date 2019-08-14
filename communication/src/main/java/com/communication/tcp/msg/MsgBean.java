package com.communication.tcp.msg;

import com.communication.tcp.base.IMessage;

import java.io.Serializable;

/**
 * Created by chenxiaojun on 2017/8/16.
 */

public class MsgBean implements Serializable {
    private static final long serialVersionUID = -7348336463862637749L;

    private String msg;
    private IMessage imessage;
    public MsgBean(String msg,IMessage iMessage){
        super();
        this.msg=msg;
        this.imessage=iMessage;

    }
    public String getMsg() {
        return msg;
    }
    public void setMsg(String msg) {
        this.msg = msg;
    }
    public IMessage getImessage() {
        return imessage;
    }
    public void setImessage(IMessage imessage) {
        this.imessage = imessage;
    }

}
