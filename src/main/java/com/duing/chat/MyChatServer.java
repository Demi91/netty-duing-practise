package com.duing.chat;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class MyChatServer {


    public static void main(String[] args) {

        // 指定线程数量的创建方式
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        // 默认创建数量  是CPU核数 * 2
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup,workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    // 服务端暂时无法处理的连接会放在请求队列中
                    // backlog 指定了队列的大小
                    .option(ChannelOption.SO_BACKLOG,128)
                    // 设置保持连接状态
                    .childOption(ChannelOption.SO_KEEPALIVE,true)
                    .childHandler(new MyChatServerInitializer());


            ChannelFuture channelFuture = serverBootstrap.bind(8899).sync();
            channelFuture.channel().closeFuture().sync();


        }catch (Exception e){

        }finally {

            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }


    }
}
