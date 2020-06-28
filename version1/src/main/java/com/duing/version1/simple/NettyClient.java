package com.duing.version1.simple;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class NettyClient {

    public static void main(String[] args) {

        // 客户端只需要一个事件循环组
        EventLoopGroup group = new NioEventLoopGroup();

        try {
            // 创建客户端启动对象
            Bootstrap bootstrap = new Bootstrap();

            // 同样是链式编程
            bootstrap.group(group)  // 设置线程组
                      // 设置客户端通道的实现类
                     .channel(NioSocketChannel.class)
                      // 设置通道对应的处理器  仍然使用通道初始化器
                     .handler(new ChannelInitializer<SocketChannel>() {

                         @Override
                         protected void initChannel(SocketChannel ch) throws Exception {
                             // 在管道中  加入自定义的处理器
                             ch.pipeline().addLast(new NettyClientHandler());
                         }
                     });

            System.out.println("客户端初始化完成");

            // 启动客户端连接服务器  阻塞
            ChannelFuture channelFuture = bootstrap.connect("127.0.0.1",8888).sync();

            // 给通道关闭监听
            channelFuture.channel().closeFuture().sync();

        }catch (Exception e){

        }finally {

            group.shutdownGracefully();
        }
    }
}
