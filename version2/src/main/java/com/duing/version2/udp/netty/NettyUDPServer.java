package com.duing.version2.udp.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.CharsetUtil;

public class NettyUDPServer {

    public static void main(String[] args) {

        EventLoopGroup group = new NioEventLoopGroup();
        // 统一使用Bootstrap进行启动和参数设置
        Bootstrap bootstrap = new Bootstrap();
        // 实现udp协议  所使用的通道 NioDatagramChannel
        bootstrap.group(group).channel(NioDatagramChannel.class)
                .localAddress(6789).handler(new UDPServerHandler());

        try {
            ChannelFuture future = bootstrap.bind(6789).sync();
            future.channel().closeFuture().await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }

    }
}

// DatagramPacket 是udp使用的数据格式
// 注意 使用的是netty封装的  包引用地址是netty包下
class UDPServerHandler extends SimpleChannelInboundHandler<DatagramPacket> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) throws Exception {

        System.out.println("receive packet :" + msg.content());
        ByteBuf buf = Unpooled.copiedBuffer("Sth", CharsetUtil.UTF_8);
        // 通过组装bytebuf  获取发送报文的发送者  得到要返回的报文
        DatagramPacket packet = new DatagramPacket(buf, msg.sender());
        ctx.writeAndFlush(packet);
    }
}
