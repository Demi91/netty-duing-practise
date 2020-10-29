package com.duing.udp.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.internal.SocketUtils;

public class NettyUDPClient {

    public static void main(String[] args) {
        EventLoopGroup group = new NioEventLoopGroup();

        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioDatagramChannel.class)
                .remoteAddress("127.0.0.1", 6789)
                .handler(new NettyUDPClientHandler());


        try {
            Channel channel = bootstrap.bind(0).sync().channel();

            // 字符串  ByteBuf  DatagramPacket  写入通道
            ByteBuf byteBuf = Unpooled.copiedBuffer(
                    "hello netty udp server".getBytes());
            DatagramPacket sendPacket = new DatagramPacket(byteBuf,
                    SocketUtils.socketAddress("127.0.0.1", 6789));

            channel.writeAndFlush(sendPacket);

        } catch (Exception e) {

        } finally {
            group.shutdownGracefully();
        }
    }
}
