package com.lte.ui.listener;

import org.apache.mina.core.session.IoSession;

/**
 * Created by chenxiaojun on 2017/9/5.
 */

public interface TcpListener {
    void messageReceived(IoSession session, Object message);
    void messageSent(IoSession session, Object message);
    void sessionClosed(IoSession session);
    void sessionCreated(IoSession session);
    void sessionOpened(IoSession session);

}
