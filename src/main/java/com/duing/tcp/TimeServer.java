package com.duing.tcp;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.util.Date;

public class TimeServer {

    public static void main(String[] args) {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {

            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {

                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            // 基于分隔符的解码器
                            ch.pipeline().addLast(new LineBasedFrameDecoder(1024));
                            // 字符串解码器
                            ch.pipeline().addLast(new StringDecoder());

                            ch.pipeline().addLast(new TimeServerHandler());
                        }
                    });

            // 启动
            ChannelFuture channelFuture = serverBootstrap.bind(0101).sync();
            channelFuture.channel().closeFuture().sync();

        } catch (Exception e) {

        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }


    static class TimeServerHandler extends ChannelInboundHandlerAdapter {

        // 请求次数计数
        int count;

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            /**
             ByteBuf buf = (ByteBuf) msg;
             // 声明数组接收其内容
             byte[] req = new byte[buf.readableBytes()];
             buf.readBytes(req);

             // 请求的长度 - 系统分隔符的长度 = 数据的长度   如：字节流 ABC/r/n
             // 将数组转为字符串后  截取
             // System.getProperty("line.separator") 代表系统所支持的分隔符
             //  windows和linux支持的不同
             String data = new String(req, "UTF-8").substring(0,
             req.length - System.getProperty("line.separator").length());
             */

            String data = (String) msg;

            String timeStr = new Date().toString();
            String currentTime = "Query Data :" + data + "; current time is " + timeStr
                    + "; count is " + ++count;

            System.out.println(currentTime);

            ByteBuf resp = Unpooled.copiedBuffer(currentTime.getBytes());
            ctx.writeAndFlush(resp);

        }
    }
}
