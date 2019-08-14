package com.communication.tcp;



import com.communication.utils.LETLog;

import java.util.HashMap;
import java.util.Map;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * Created by chenxiaojun on 2017/8/18.
 * Tcp 客户端管理
 */

public class Client {

    private static volatile Client client;

    private EventLoopGroup workerGroup;

    private Bootstrap mBootstrap;

    private Map<String,TcpService> tcpMap;

    private Channel channel;

    private Client() {
        tcpMap = new HashMap<>();
    }

    public static Client getInstance() {
        if (client == null) {
            synchronized (Client.class) {
                if (client == null) {
                    client = new Client();
                }
            }
        }
        return client;
    }
    public void addTcpService(final TcpService tcpService){
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(tcpMap.containsKey(tcpService.getIp())){
                    if(tcpMap.get(tcpService.getIp()).isOpen()){
                        return;
                    }else{
                        tcpMap.get(tcpService.getIp()).close();
                        tcpMap.remove(tcpService.getIp());
                    }
                }
                try {
                    connectTcpService(tcpService);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    public synchronized boolean connectTcpService(TcpService tcpService) throws Exception{
        LETLog.d("TcpService ---connectTcpService" + tcpService.getIp() + " ---" + tcpService.getPort());
        getBootstrap();
        this.mBootstrap.handler(new ClientChannelInitializer(tcpService));
        channel = this.mBootstrap.connect(tcpService.getIp(), tcpService.getPort()).sync().channel();
        tcpService.setChannel(channel);
        tcpMap.put(tcpService.getIp(),tcpService);
        return true;
    }
    /**
     * @return void
     * @throws
     * @Title: getBootstrap
     * @Description: TODO(开启工作线程和监听线程)
     */
    private void getBootstrap() {
        if (this.workerGroup == null || this.workerGroup.isTerminated()) {
            this.workerGroup = new NioEventLoopGroup();
        }
        if (this.mBootstrap == null) {
            this.mBootstrap = new Bootstrap();
            this.mBootstrap.group(workerGroup);
            this.mBootstrap.channel(NioSocketChannel.class);
            this.mBootstrap.option(ChannelOption.SO_KEEPALIVE, true);
        }
    }
    /**
     * @return void
     * @throws
     * @Title: releaseConnected
     * @Description: TODO(释放连接)
     */
    public void releaseConnected() {
        for(TcpService tcpService:tcpMap.values()){
            tcpService.close();
        }
        /**释放是关闭工作线程*/
        if (workerGroup != null) {
            workerGroup.shutdownGracefully();
            workerGroup = null;
        }
        if (mBootstrap != null) {
            mBootstrap = null;
        }
    }
    /**
     * @return void
     * @throws
     * @Title: releaseConnected
     * @Description: TODO(释放连接)
     */
    public void releaseConnected(String ip) {
        if(tcpMap.containsKey(ip)){
            TcpService tcpService = tcpMap.get(ip);
            tcpService.close();
            tcpMap.remove(ip);
        }
        if(tcpMap.size() == 0){
            /**释放是关闭工作线程*/
            if (workerGroup != null) {
                workerGroup.shutdownGracefully();
                workerGroup = null;
            }
            if (mBootstrap != null) {
                mBootstrap = null;
            }
        }
    }

    public boolean isLocal(String ip) {
        if(!tcpMap.containsKey(ip)){
            return false;
        }
        return tcpMap.get(ip).isOpen();
    }
}
