package com.communication.tcp.base;

/**
 * Created by chenxiaojun on 2017/8/16.
 */

public interface IClient {
    /**
     * @param hostName
     * @param port
     * @param iHandler
     * @return IChannel
     * @throws Exception
     * @throws
     * @Title: connect
     * @Description: TODO(创建Socket)
     */
    IChannel connect(String hostName, int port, IHandler iHandler) throws Exception;

    /**
     * @return boolean
     * @throws Exception
     * @throws
     * @Title: disconnect
     * @Description: TODO(断开连接)
     */
    boolean disconnect(String ip) throws Exception;
}
