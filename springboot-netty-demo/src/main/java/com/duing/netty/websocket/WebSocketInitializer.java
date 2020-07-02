package com.duing.netty.websocket;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class WebSocketInitializer extends ChannelInitializer<SocketChannel> {

    @Autowired
    private WebSocketHandler webSocketHandler;

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        // 增加编解码器的另一种方式
        pipeline.addLast(new HttpServerCodec());
        // 块方式写的处理器  用于处理较大数据
        pipeline.addLast(new ChunkedWriteHandler());
        // 聚合
        pipeline.addLast(new HttpObjectAggregator(512*1024));
        // 声明websocket的请求路径
        //  ws://127.0.0.1:8899/chat
        //  是将http协议升级为websocket协议  并且使用101作为响应码
        pipeline.addLast(new WebSocketServerProtocolHandler("/chat"));

        pipeline.addLast(webSocketHandler);
    }
}
