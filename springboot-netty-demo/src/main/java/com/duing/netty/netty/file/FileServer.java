package com.duing.netty.netty.file;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

// http的文件服务Demo
// 通过http请求  访问指定目录的文件列表
@Component
public class FileServer {

    private EventLoopGroup bossGroup = new NioEventLoopGroup();
    private EventLoopGroup workerGroup = new NioEventLoopGroup();

    private Channel channel;

    @Autowired
    private FileServerHandler serverHandler;

    public ChannelFuture start(int port) {

        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new ChannelInitializer<SocketChannel>() {
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
                });

        ChannelFuture future = null;
        try {
            future = serverBootstrap.bind(port).sync();
            channel = future.channel();
//            future.channel().closeFuture().sync();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {

        }
        return future;
    }


    public void destroy() {
        if (channel != null) {
            channel.close();
        }
        workerGroup.shutdownGracefully();
        bossGroup.shutdownGracefully();
        System.out.println("netty server shutdown");

    }

}
