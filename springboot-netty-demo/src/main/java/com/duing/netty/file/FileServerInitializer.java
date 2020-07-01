package com.duing.netty.file;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

// http的文件服务Demo
// 通过http请求  访问指定目录的文件列表
@Component
public class FileServerInitializer extends ChannelInitializer<SocketChannel> {

    @Autowired
    private FileServerHandler serverHandler;

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        // 使用netty提供的http处理器
        // 处理http消息的编解码器
        pipeline.addLast(new HttpServerCodec());
        // 处理http消息的聚合器（分段传输） 能接受的最大范围是512KB
        pipeline.addLast(new HttpObjectAggregator(512 * 1024));
        // 支持大数据的文件传输  并且控制对内存的使用
        pipeline.addLast(new ChunkedWriteHandler());
        // 增加自定义处理器
        pipeline.addLast(serverHandler);
    }
}
