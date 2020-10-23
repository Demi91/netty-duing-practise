package com.duing.redis;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.redis.RedisArrayAggregator;
import io.netty.handler.codec.redis.RedisBulkStringAggregator;
import io.netty.handler.codec.redis.RedisDecoder;
import io.netty.handler.codec.redis.RedisEncoder;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class RedisClient {

    private static final String HOST = "192.168.1.12";
    private static final int PORT = 6379;

    public static void main(String[] args) throws Exception {

        EventLoopGroup group = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        // 使用  netty提供的  redis协议相关的  编解码器
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new RedisDecoder());
                        // 是 redis 某一种数据格式  使用的聚合器
                        pipeline.addLast(new RedisBulkStringAggregator());
                        pipeline.addLast(new RedisArrayAggregator());

                        pipeline.addLast(new RedisEncoder());

                        // 自定义的处理器
                        pipeline.addLast(new RedisClientHandler());
                    }
                });

        System.out.println("客户端初始化完成");
        try {
            Channel channel = bootstrap.connect(HOST, PORT).sync().channel();

            System.out.println("Enter Redis Command:");
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(System.in));

            // 接收键盘输入
            for(;;){
                String input = in.readLine();
                channel.writeAndFlush(input).sync();
            }
//            future.channel().closeFuture().sync();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }
}
