package com.communication.tcp;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.communication.tcp.msg.HeartMsg;
import com.communication.tcp.msg.ReceiveMsg;
import com.communication.utils.Constant;
import com.communication.utils.LETLog;

import java.lang.ref.WeakReference;
import java.util.concurrent.TimeUnit;

import io.netty.channel.Channel;
import io.netty.channel.EventLoop;

/**
 * Created by chenxiaojun on 2017/8/18.
 */

public class TcpService implements TcpListener{

    private static final String TAG = "TcpService";
    private HeartMsg heartMsg;

    private long id;

    private String ip;

    private int port;

    private Channel channel;

    private TcpMsgCallback tcpMsgCallback;

    private static final int START_HEART = 0x01;

    private static final long HEART_DELAY = 5*1000l;

    private MHandler mHandler;


    public TcpService(String ip,int port){
        this.ip = ip;
        this.port = port;
        mHandler = new MHandler(this);
        heartMsg = new HeartMsg(Constant.DEFAULT_HEART_MSG_BODY);
    }

    private static class MHandler extends Handler{
        private WeakReference<TcpService> reference;


        MHandler(TcpService tcpService) {
            reference = new WeakReference<>(tcpService);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case START_HEART:
                    reference.get().sendHeartMsg();
                    startHeart();
                    break;
            }
        }
        public void startHeart(){
            sendEmptyMessageDelayed(START_HEART,HEART_DELAY);
        }
        public void removeHeart(){
            removeMessages(START_HEART);
        }

    }
    public void sendHeartMsg(){
        sendMsg(heartMsg.getMsgBody());
    }
    @Override
    public void connect(Channel channel) throws Exception {
//        mHandler.startHeart();
        this.channel = channel;
        LETLog.d("TCP  connect  --- ok");
    }


    private void reConnect(){
        try {
            Client.getInstance().connectTcpService(this);
        }
        catch (Exception ex){
            LETLog.d(ex.toString());
        }
    }

    @Override
    public void disConnect(Channel channel) throws Exception {
//        mHandler.removeHeart();

        //this.close();
        LETLog.d("TcpService:disconect,ip="+ip+"port="+port);
        LETLog.d("TcpService:reconect,ip="+ip+"port="+port);

        final EventLoop eventLoop = channel.eventLoop();
        eventLoop.schedule(new Runnable() {
            @Override
            public void run() {
                reConnect();
            }
        }, 1L, TimeUnit.SECONDS);


    }

    @Override
    public void exception(Channel channel,Throwable cause) throws Exception {

    }

    @Override
    public void receiveMsg(Channel channel,Object msg) throws Exception {
        LETLog.d("TcpService:  receiveMsg" +(tcpMsgCallback == null) + " ---" + (msg instanceof ReceiveMsg));
        if(tcpMsgCallback != null){
            if(msg instanceof ReceiveMsg){
                ReceiveMsg msg1 = (ReceiveMsg) msg;
                tcpMsgCallback.receiveMsg(msg1.getMsgBody());
            }
        }
    }

    public synchronized void sendMsg(String msg, TcpMsgCallback callback){
        tcpMsgCallback = callback;
        sendMsg(msg);

    }
    private synchronized void sendMsg(String msg){
        if (channel == null || !isOpen() || TextUtils.isEmpty(msg)) {
            return;
        }
        LETLog.d("TcpService ---MSG :" + msg + "channel :" + channel + "isOpen :" + channel.isOpen());
        channel.writeAndFlush(msg);
    }
    public String combineMsg(String msg) {
        StringBuffer stringBuffer = new StringBuffer();
        String hex = Integer.toHexString(msg.length());
        String str = hex;
        if (hex.length() == 1) {
            str = "000" + hex;
        } else if (hex.length() == 2) {
            str = "00" + hex;
        } else if (hex.length() == 3) {
            str = "0" + hex;
        }
        stringBuffer.append("##").append(str)
                .append(msg).append("&&\n");
        return stringBuffer.toString();
    }
    public boolean isOpen(){
        return channel.isOpen();
    }

    public void close(){
        try {
            channel.closeFuture().sync();
        } catch (InterruptedException e) {
        }
        channel.close();
    }

    public boolean isActive(){
        return channel.isActive();
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public HeartMsg getHeartMsg() {
        return heartMsg;
    }

    public void setHeartMsg(HeartMsg heartMsg) {
        this.heartMsg = heartMsg;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }
}
