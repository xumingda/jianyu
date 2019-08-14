package com.communication.tcp;


import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;

/**
 * Created by chenxiaojun on 2017/8/16.
 */

public class ClientChannelInitializer extends ChannelInitializer<Channel> {
    public TcpListener tcpListener;

    public ClientChannelInitializer(TcpListener tcpListener) {
        this.tcpListener = tcpListener;
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast("decoder", new ClientDecoder());
        pipeline.addLast("encoder", new ClientEncoder());
        pipeline.addLast("handler", new ClientHandler(tcpListener));
    }

}
