package com.duing.simple;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

/**
 * 自定义的handler 需要继承 netty提供的 HandlerAdapter
 */
public class NettyServerHandler extends ChannelInboundHandlerAdapter {


    /**
     * 在通道被启用，即为建立连接时，触发此方法
     * 通常用于  写入欢迎消息等
     * <p>
     * 参数 ChannelHandlerContext 是上下文对象
     * 可以获取 通道channel  管道pipeline
     * <p>
     * 写入数据时  调用writeAndFlush()  代表写入并刷新  只有刷新后才生效
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channelActive done");
        ctx.writeAndFlush("Welcome to My Netty Server");

        super.channelActive(ctx);
    }


    /*
     * 读取数据的方法
     * 其中参数msg  是客户端发送的数据
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 对应于NIO中的ByteBuffer  -> ByteBuf
        ByteBuf buf = (ByteBuf) msg;
//        System.out.println("client msg : " + buf.toString());
        System.out.println("client address : " + ctx.channel().remoteAddress());
        System.out.println("client msg : " + buf.toString(CharsetUtil.UTF_8));
    }


    // 数据读取完成会触发的方法
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        // 写入的数据还是使用ByteBuf处理   其中一个分类叫做Unpooled
        ctx.writeAndFlush(Unpooled.copiedBuffer("hello client , I am server", CharsetUtil.UTF_8));
    }


}
