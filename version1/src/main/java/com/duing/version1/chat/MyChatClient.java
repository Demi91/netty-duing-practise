package com.duing.version1.chat;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class MyChatClient {

    public static void main(String[] args) {

        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();

        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(eventLoopGroup)
                    .channel(NioSocketChannel.class)
                    .handler(new MyChatClientInitializer());

            Channel channel = bootstrap.connect("127.0.0.1", 8899).sync().channel();

            //键盘接收输入
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            for (; ; ) {
                String msg = br.readLine();
                channel.writeAndFlush(msg + "\r\n");
            }


        } catch (Exception e) {

        } finally {
            eventLoopGroup.shutdownGracefully();
        }

    }
}
