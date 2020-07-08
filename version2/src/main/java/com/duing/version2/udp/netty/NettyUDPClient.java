package com.duing.version2.udp.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.CharsetUtil;
import io.netty.util.internal.SocketUtils;

public class NettyUDPClient {

    public static void main(String[] args) {

        EventLoopGroup group = new NioEventLoopGroup();
        // 统一使用Bootstrap进行启动和参数设置
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group).channel(NioDatagramChannel.class)
                .remoteAddress("127.0.0.1", 6789)
                .handler(new UDPClientHandler());

        try {

            Channel channel = bootstrap.bind(0).sync().channel();
            ByteBuf buf = Unpooled.copiedBuffer("Send Sth", CharsetUtil.UTF_8);
            // 通过组装bytebuf  获取发送报文的发送者  得到要返回的报文
            DatagramPacket packet = new DatagramPacket(buf,
                    SocketUtils.socketAddress("127.0.0.1", 6789));
            channel.writeAndFlush(packet).sync();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }

    }
}

class UDPClientHandler extends SimpleChannelInboundHandler<DatagramPacket> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg)
            throws Exception {

        String respStr = msg.content().toString(CharsetUtil.UTF_8);
        System.out.println(respStr);
        ctx.close();

    }
}