package com.duing.version1.simple;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

// 同样继承  通道处理器的适配器
public class NettyClientHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
//        ctx.fireChannelActive();
        System.out.println("client channelActive done");
        ctx.writeAndFlush(Unpooled.copiedBuffer("hello server,I am client",
                CharsetUtil.UTF_8));
    }


    // 读事件发生时触发
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        ctx.fireChannelRead(msg);
        ByteBuf buf = (ByteBuf) msg;
        System.out.println("server address : " + ctx.channel().remoteAddress());
        System.out.println("server msg : " + buf.toString(CharsetUtil.UTF_8));
    }

}
