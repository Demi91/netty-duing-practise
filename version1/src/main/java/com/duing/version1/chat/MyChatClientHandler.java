package com.duing.version1.chat;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;


public class MyChatClientHandler extends SimpleChannelInboundHandler {

    protected void channelRead0(ChannelHandlerContext ctx, Object msg)
            throws Exception {
        System.out.println(msg);
    }
}
