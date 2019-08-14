package com.lte.tcpserver;

import android.os.Bundle;
import android.util.Log;

import com.lte.ui.listener.TcpListener;

import org.apache.mina.core.future.CloseFuture;
import org.apache.mina.core.future.IoFuture;
import org.apache.mina.core.future.IoFutureListener;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.transport.socket.SocketSessionConfig;

import java.util.ArrayList;
import java.util.HashMap;

import static com.lte.utils.AppUtils.bytesToHexString;

/**
 * Created by chenxiaojun on 2017/8/28.
 */

public class MyServerHandler extends IoHandlerAdapter {
    private static String TAG = "MyServerHandler";

    private TcpListener listener;

    public MyServerHandler(TcpListener listener) {
        this.listener = listener;
    }

    public void setListener(TcpListener listener) {
        this.listener = listener;
    }

    // 从端口接受消息，会响应此方法来对消息进行处理
    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {
        ArrayList<Integer> msg = (ArrayList<Integer>) message;
        Bundle bundle = new Bundle();
        if (listener != null) {
            listener.messageReceived(session, msg);
        }
    }

    public static String bytesToHexString(byte[] bArray) {
        if (bArray == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder(bArray.length);
        String sTemp;
        for (byte aBArray : bArray) {
            sTemp = Integer.toHexString(0xFF & aBArray);
            if (sTemp.length() < 2)
                sb.append(0);
            sb.append(sTemp.toUpperCase());
            sb.append(",");
        }
        return sb.toString();
    }

    // 向客服端发送消息后会调用此方法
    @Override
    public void messageSent(IoSession session, Object message) throws Exception {
        super.messageSent(session, message);
        if (listener != null) {
            listener.messageSent(session, message);
        }
    }

    // 关闭与客户端的连接时会调用此方法
    @Override
    public void sessionClosed(IoSession session) throws Exception {
        if (listener != null) {
            listener.sessionClosed(session);
        }
        CloseFuture closeFuture = session.close(true);
        closeFuture.addListener(new IoFutureListener<IoFuture>() {
            public void operationComplete(IoFuture future) {
                if (future instanceof CloseFuture) {
                    ((CloseFuture) future).setClosed();
                }
            }

            ;
        });
        session.close(true);// 关闭session
    }

    // 服务器与客户端创建连接
    @Override
    public void sessionCreated(IoSession session) throws Exception {
        SocketSessionConfig cfg = (SocketSessionConfig) session.getConfig();
        cfg.setReceiveBufferSize(1024);
        cfg.setReadBufferSize(1024);
        cfg.setKeepAlive(true);
        cfg.setSoLinger(0);
        if (listener != null) {
            listener.sessionCreated(session);
        }
    }

    // 服务器与客户端连接打开
    @Override
    public void sessionOpened(IoSession session) throws Exception {
        Log.d(TAG, "服务器与客户端连接打开...");
        super.sessionOpened(session);
        if (listener != null) {
            listener.sessionOpened(session);
        }
    }

    @Override
    public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
        Log.d(TAG, "服务器进入空闲状态...");
        super.sessionIdle(session, status);
    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
        Log.d(TAG, "服务器发送异常...");
        super.exceptionCaught(session, cause);
    }
}
