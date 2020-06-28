package com.duing.version1.tcp;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

public class TimeClient {

    public static void main(String[] args) {

        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();

        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(eventLoopGroup)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .handler(new ChannelInitializer<SocketChannel>() {

                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new LineBasedFrameDecoder(1024));
                            ch.pipeline().addLast(new StringDecoder());
                            ch.pipeline().addLast(new TimeClientHandler());
                        }
                    });

            Channel channel = bootstrap.connect("127.0.0.1", 0101).sync().channel();
            channel.closeFuture().sync();

        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            eventLoopGroup.shutdownGracefully();
        }

    }


    static class TimeClientHandler extends ChannelInboundHandlerAdapter {

        private int count;
        private byte[] request;

        public TimeClientHandler() {
            request = ("query time" + System.getProperty("line.separator")).getBytes();
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            System.out.println("channel active");

            ByteBuf message = null;
            for (int i = 0; i < 100; i++) {
                message = Unpooled.buffer(request.length);
                message.writeBytes(request);
                ctx.writeAndFlush(message);

            }
        }


        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//            super.channelRead(ctx, msg);
//            ByteBuf buf = (ByteBuf) msg;
//            byte[] tmp = new byte[buf.readableBytes()];
//            buf.readBytes(tmp);
//
//            String data = new String(tmp, "UTF-8");
            String data = (String) msg;
            System.out.println("data is : " + data + "; count is " + ++count);
        }
    }
}
