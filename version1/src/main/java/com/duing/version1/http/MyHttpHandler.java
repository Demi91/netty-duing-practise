package com.duing.version1.http;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;

/**
 * 泛型设置为 FullHttpRequest
 * 筛选为 msg 是此类型的消息才处理
 */
public class MyHttpHandler extends SimpleChannelInboundHandler<FullHttpRequest> {


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {

        // 设定 http版本  响应码  响应数据
        DefaultFullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1,
                HttpResponseStatus.OK,
                Unpooled.wrappedBuffer("hello http netty demo".getBytes())
        );

        // HttpHeaders 可以设置请求头
        // HttpHeaderNames 提供了请求头的字段
        // HttpHeaderValues  提供了请求头字段的常用参数
        HttpHeaders headers = response.headers();
        headers.add(HttpHeaderNames.CONTENT_TYPE,
                HttpHeaderValues.TEXT_PLAIN + ";charset=UTF-8");
        // 确保请求或响应被完整处理  要记得设置长度
        headers.add(HttpHeaderNames.CONTENT_LENGTH,response.content().readableBytes());
        headers.add(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);

        ctx.write(response);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        super.channelReadComplete(ctx);
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        cause.printStackTrace();
        // 关闭上下文  即通道
        ctx.close();
    }
}
