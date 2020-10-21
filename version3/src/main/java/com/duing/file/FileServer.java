package com.duing.file;

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

public class FileServer {

    public static void main(String[] args) {

        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        ServerBootstrap serverBootstrap = new ServerBootstrap();

        // 链式编程
        serverBootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                // 接收到客户端的通道之后  后续如何处理的逻辑
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        // 管家  管理处理器的运行和顺序
                        ChannelPipeline pipeline = ch.pipeline();
                        // 设置http相关的处理器
                        // http消息的编解码器
                        pipeline.addLast(new HttpServerCodec());
                        // 聚合器的最大范围 512kb
                        pipeline.addLast(new HttpObjectAggregator(512 * 1024));
                        // 支持大数据的文件传输  控制内存使用
                        pipeline.addLast(new ChunkedWriteHandler());

                        // 自定义
                        pipeline.addLast(new FileHandler());

                    }
                })
        ;

        try {
            ChannelFuture future = serverBootstrap.bind(1010).sync();
            // 阻塞关闭操作
            future.channel().closeFuture().sync();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {

            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }
}
