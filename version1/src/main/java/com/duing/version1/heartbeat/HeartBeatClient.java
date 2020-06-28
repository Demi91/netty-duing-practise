package com.duing.version1.heartbeat;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.util.Random;

public class HeartBeatClient {

    int port;
    Random random;

    public HeartBeatClient(int port) {
        this.port = port;
        random = new Random();
    }


    public static void main(String[] args) {
        HeartBeatClient client = new HeartBeatClient(9999);
        client.start();
    }


    public void start() {

        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();

        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(eventLoopGroup)
                    .channel(NioSocketChannel.class)
                    .handler(new HeartBeatInitializer());

            Channel channel = bootstrap.connect("127.0.0.1", port).sync().channel();
            // 要发送的数据
            String msg = "I am alive";
            // 只要通道是活跃的  就发送消息
            while (channel.isActive()) {
                int num = random.nextInt(10);
                System.out.println("wait time: " + num);
                Thread.sleep(num * 1000);

                System.out.println("seng msg: " + msg);
                channel.writeAndFlush(msg);
            }

        } catch (Exception e) {

        } finally {
            eventLoopGroup.shutdownGracefully();
        }
    }


    static class HeartBeatInitializer extends ChannelInitializer<Channel> {

        @Override
        protected void initChannel(Channel ch) throws Exception {
            ChannelPipeline pipeline = ch.pipeline();
            pipeline.addLast(new StringDecoder())
                    .addLast(new StringEncoder())
                    .addLast(new HeartBeatClientHandler());

        }
    }

    static class HeartBeatClientHandler extends SimpleChannelInboundHandler<String> {

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
            System.out.println("client receive message: " + msg);

            if (msg != null && msg.equals("you are out")) {
                System.out.println("client will close");
                ctx.channel().closeFuture();
            }
        }
    }
}
