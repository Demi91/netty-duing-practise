package com.duing.udp.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.CharsetUtil;

public class NettyUDPSeverHandler extends
        SimpleChannelInboundHandler<DatagramPacket> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg)
            throws Exception {

        System.out.println(msg.content().toString(CharsetUtil.UTF_8));

        // 字符串  ByteBuf  DatagramPacket  写入通道
        ByteBuf byteBuf = Unpooled.copiedBuffer(
                "hello I don't know who you are".getBytes());
        DatagramPacket receivedPacket = new DatagramPacket(byteBuf, msg.sender());
        ctx.write(receivedPacket);

    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
//        super.channelReadComplete(ctx);
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {

        // 记录日志  方便追踪异常问题
//        super.exceptionCaught(ctx, cause);
    }
}
