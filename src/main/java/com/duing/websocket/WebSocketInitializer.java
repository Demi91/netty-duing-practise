package com.duing.websocket;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;


public class WebSocketInitializer extends ChannelInitializer<SocketChannel> {


    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        // 因为仍基于http协议  仍使用其编码解码器
        pipeline.addLast(new HttpServerCodec());

        // 因为以块方式写  需要使用ChunkedWriteHandler
        pipeline.addLast(new ChunkedWriteHandler());

        // 因为http数据是分段的   使用聚合器
        pipeline.addLast(new HttpObjectAggregator(512 * 1024));

        // 使用websocket服务端协议处理器   对请求地址进行定位
        // ws://127.0.0.1:7777/hello 进行请求
        // 此处理器将http升级为websocket  使其保持长连接  使用状态码101
        pipeline.addLast(new WebSocketServerProtocolHandler("/hello"));

        // 自定义处理器
        pipeline.addLast(new WebSocketHandler());


    }
}
