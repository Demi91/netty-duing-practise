package com.duing.version1.heartbeat;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

public class HeartBeatServer {

    int port;

    public HeartBeatServer(int port) {
        this.port = port;
    }

    public void start() {

        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {

            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new HeartBeatInitializer());

            // 启动
            ChannelFuture channelFuture = serverBootstrap.bind(port).sync();
            channelFuture.channel().closeFuture().sync();

        } catch (Exception e) {

        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }


    static class HeartBeatInitializer extends ChannelInitializer<Channel>{

        @Override
        protected void initChannel(Channel ch) throws Exception {
            ChannelPipeline pipeline = ch.pipeline();

            // 使用字符串的编码解码器
            pipeline.addLast(new StringDecoder());
            pipeline.addLast(new StringEncoder());

            // 使用空闲状态处理器
            //  检测的是 IdleStateEvent事件  通过管道传递给下一个handler处理
            //  userEventTriggered
            pipeline.addLast(new IdleStateHandler(2,3,5, TimeUnit.SECONDS));

            pipeline.addLast(new HeartBeatHandler());
        }
    }


    public static void main(String[] args) {
        HeartBeatServer server = new HeartBeatServer(9999);
        server.start();
    }
}
