package com.duing.version2.redis;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
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

    public static void main(String[] args) {

        EventLoopGroup group = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        // 调用netty提供的 支持redis协议的编解码器
                        // RedisBulkStringAggregator和RedisArrayAggregator
                        //  是协议中某两种特殊格式的聚合器
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new RedisDecoder());
                        pipeline.addLast(new RedisBulkStringAggregator());
                        pipeline.addLast(new RedisArrayAggregator());
                        pipeline.addLast(new RedisEncoder());

                        pipeline.addLast(new RedisClientHandler());
                    }
                })
        ;

        try {
            Channel channel = bootstrap.connect(HOST,PORT).sync().channel();
            System.out.println("enter redis commands: ");

            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            for(;;){
                String input = in.readLine();
                // 退出命令
                if("quit".equals(input)){
                    channel.close().sync();
                    break;
                }

                channel.writeAndFlush(input).sync();
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            group.shutdownGracefully();
        }

    }

}
