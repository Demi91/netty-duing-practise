package com.duing.udp.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;

public class NettyUDPServer {

    static final int port = 6789;

    public static void main(String[] args) {
        EventLoopGroup group = new NioEventLoopGroup();

        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioDatagramChannel.class)
                    .localAddress(port)
                    .handler(new NettyUDPSeverHandler());

            bootstrap.bind(port).sync().channel().closeFuture().await();

        } catch (Exception e) {

        } finally {
            group.shutdownGracefully();
        }
    }
}
