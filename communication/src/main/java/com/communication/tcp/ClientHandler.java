package com.communication.tcp;

import android.util.Log;


import com.communication.utils.LETLog;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * Created by chenxiaojun on 2017/8/16.
 */

public class ClientHandler extends ChannelInboundHandlerAdapter {
    private TcpListener mTcpListener;
    private static final String TAG = "ClientHandler";

    public ClientHandler(TcpListener iHandler) {
        mTcpListener = iHandler;
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Log.i(TAG, ctx.channel().toString()+"----channelActive----连接ok---");
        LETLog.d("scoket----channelActive----连接ok---" + mTcpListener);
//        ClientChannel clientChannel = new ClientChannel(ctx.channel());
        mTcpListener.connect(ctx.channel());
    }


    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Log.d(TAG, ctx.channel().toString()+"----channelInactive----断连------");
        LETLog.d("scoket----channelInactive----断连------");
        mTcpListener.disConnect(ctx.channel());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Log.v(TAG, ctx.channel().toString()+"---------channelRead--------->" + msg.toString());
        LETLog.d("socketReceive:" +msg.toString());
        mTcpListener.receiveMsg(ctx.channel(), msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        Log.i(TAG, ctx.channel().toString()+"---exceptionCaught-----6----" + cause.getLocalizedMessage());
//        ClientChannel clientChannel = new ClientChannel(ctx.channel());
        mTcpListener.exception(ctx.channel(),cause);
        LETLog.d("scoket----channelInactive----exceptionCaught------");
    }


    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        super.channelUnregistered(ctx);
        LETLog.d("scoket----channelInactive----channelUnregistered------");

    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        super.channelReadComplete(ctx);
        LETLog.d("scoket----channelInactive----channelReadComplete------");
    }
}
