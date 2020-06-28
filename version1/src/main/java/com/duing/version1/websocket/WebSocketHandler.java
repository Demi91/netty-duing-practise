package com.duing.version1.websocket;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

// 此时 泛型要代表websocket传输数据的单位
// TextWebSocketFrame
public class WebSocketHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {

        System.out.println("msg: "+ msg.text());
        Channel channel =  ctx.channel();
        TextWebSocketFrame resp = new TextWebSocketFrame("hello client from websocket server");
        channel.writeAndFlush(resp);
    }


    // 通过ctrl+O 快捷键  重写方法
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        System.out.println("handlerAdded: " + ctx.channel().id().asLongText());

        super.handlerAdded(ctx);

    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        System.out.println("handlerRemoved: " + ctx.channel().id().asLongText());
        super.handlerRemoved(ctx);
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

        ctx.close();
        super.exceptionCaught(ctx, cause);
    }
}
