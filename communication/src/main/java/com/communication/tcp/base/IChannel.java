package com.communication.tcp.base;

import io.netty.channel.Channel;

/**
 * Created by chenxiaojun on 2017/8/16.
 */

public interface IChannel {

    /**
     * @return void
     * @throws
     * @Title: close
     * @Description: TODO(关闭连接)
     */
    void close();

    /**
     * @return boolean
     * @throws
     * @Title: isOpen
     * @Description: TODO(连接是否打开)
     */
    boolean isOpen();

    /**
     * @param msg
     * @return void
     * @throws
     * @Title: sendMsg
     * @Description: TODO(消息发送)
     */
    void sendMsg(Object msg);

    String getIp();

    Channel getChannel();

}
