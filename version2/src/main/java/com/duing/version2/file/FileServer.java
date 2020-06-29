package com.duing.version2.file;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

// http的文件服务Demo
// 通过http请求  访问指定目录的文件列表
public class FileServer {

    public static void main(String[] args) {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup,workerGroup)
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
                        pipeline.addLast(new FileServerHandler());
                    }
                });


        try {
            ChannelFuture future = serverBootstrap.bind(1010).sync();
            future.channel().closeFuture().sync();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

}
