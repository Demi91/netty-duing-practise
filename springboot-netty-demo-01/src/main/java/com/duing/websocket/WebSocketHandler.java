package com.duing.websocket;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.springframework.stereotype.Component;

// 泛型 代表的是处理数据的单位
// TextWebSocketFrame 是文本信息帧
@Component
@ChannelHandler.Sharable
public class WebSocketHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        System.out.println("msg : " + msg.text());
        Channel channel = ctx.channel();
        TextWebSocketFrame resp = new TextWebSocketFrame("hello client from websocket server");
        channel.writeAndFlush(resp);
    }
}
