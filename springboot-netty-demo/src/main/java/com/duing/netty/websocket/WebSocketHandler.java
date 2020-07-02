package com.duing.netty.websocket;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.springframework.stereotype.Component;


// 泛型 代表的是处理数据的单位
// TextWebSocketFrame 是文本信息帧
@Component
@ChannelHandler.Sharable
public class WebSocketHandler extends
        SimpleChannelInboundHandler<TextWebSocketFrame> {

    public final static ChannelGroup GROUP = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);


    @Override
    protected void channelRead0(ChannelHandlerContext ctx,
                                TextWebSocketFrame msg) throws Exception {

        System.out.println("msg : " + msg.text());

        Channel channel = ctx.channel();
        TextWebSocketFrame resp = new TextWebSocketFrame("test");
        channel.writeAndFlush(resp);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // 将channel添加到channel group中
        GROUP.add(ctx.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
//        super.channelInactive(ctx);
        GROUP.remove(ctx.channel());
    }
}
