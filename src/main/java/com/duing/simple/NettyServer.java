package com.duing.simple;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * Hello world
 * <p>
 * 服务端   接收数据
 * 客户端   发送数据
 */
public class NettyServer {

    public static void main(String[] args) throws Exception {

        // EventLoopGroup 是4.x版本后提出的概念
        // 用于管理channel   使用时  通常创建一个boss  一个worker
        // 两个都会无限循环
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();


        try {

            // 服务器端的启动对象
            ServerBootstrap serverBootstrap = new ServerBootstrap();

            // 提供链式编程的方法设置其中的参数
            // 链式编程的原理 ：方法返回的都是当前对象

            // 设置两个线程组  一个处理accept事件  一个处理读写事件
            serverBootstrap.group(bossGroup, workerGroup)
                    // 服务器端的通道实现使用的是什么  通过反射去获取
                    // NioServerSocketChannel -> ServerSocketChannel -> ServerSocket
                    .channel(NioServerSocketChannel.class)
                    // 设置NioServerSocketChannel的处理
                    // 此处设置的是netty提供的日志打印处理器
                    .handler(new LoggingHandler(LogLevel.INFO))
                    // 设置连接服务端的客户端  SocketChannel的处理器
                    // 这个参数  接收的是  通道初始化的对象
                    .childHandler(new ChannelInitializer<SocketChannel>() {

                        // 设置处理器
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            // 需要在管道pipeline中设置处理器
                            // 通道是建立连接的角色  管道是管理业务处理逻辑
                            socketChannel.pipeline().addLast(new NettyServerHandler());
                        }
                    });


            System.out.println("服务端初始化完成");

            // 绑定端口 让客户端来连接
            // 阻塞等待连接   可以理解为启动服务端
            ChannelFuture channelFuture = serverBootstrap.bind(8888).sync();

            //关闭监听
            channelFuture.channel().closeFuture().sync();

        } catch (Exception e) {


        } finally {
            // 优雅关闭
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }


}
