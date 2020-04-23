package com.duing.chat;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.net.InetAddress;
import java.util.Date;
import java.util.Iterator;

public class MyChatServerHandler extends SimpleChannelInboundHandler {

    // 当多个通道传入handler  netty提供了channel组的管理方式
    // GlobalEventExecutor 是一个全局事件执行器  单例
    private static ChannelGroup channelGroup = new DefaultChannelGroup(
            GlobalEventExecutor.INSTANCE);

    // 读数据的处理逻辑
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg)
            throws Exception {

        Channel selfChannel = ctx.channel();
        // 能接收客户端的消息
        // 再广播给其他客户端
        Iterator<Channel> iterator = channelGroup.iterator();
        while (iterator.hasNext()) {
            Channel channel = iterator.next();
            // 遍历的通道 不是发送消息的通道
            if (selfChannel != channel) {
                channel.writeAndFlush("[服务器] - " + selfChannel.remoteAddress()
                        + " 发送消息 ： " + msg + "\n");
                continue;
            }

            String answer;
            if (((String) msg).length() == 0) {
                answer = "Please say something \r\n";
            } else {
                answer = "Did you say " + msg + "? \r\n";
            }
            channel.writeAndFlush(answer);
        }


    }

    // 刚刚建立连接时  第一个被执行的方法
    // 常常用于 将channel加入到channel组中
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        ctx.writeAndFlush("[服务器] - " + channel.remoteAddress() + "连接成功 \n");

        channelGroup.add(channel);
    }

    // 连接被移除或者被断开  最后被执行的方法
    // 自动将channel从channel组中移除
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {

        Channel channel = ctx.channel();
        ctx.writeAndFlush("[服务器] - " + channel.remoteAddress() + "断开连接 \n");
        System.out.println("channel group size : " + channelGroup.size());
    }


    // 连接成功  此时通道是活跃的
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        // 当写多条数据时   写时使用write  写完成后flush
        // 发送欢迎消息
        ctx.write("Welcome to " + InetAddress.getLocalHost().getHostName() + "!\r\n");
        ctx.write("It is " + new Date() + " now. \r\n");

        ctx.flush();

        System.out.println(channel.remoteAddress() + " 上线");

    }


    // 此时通道是不活跃的
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        System.out.println(channel.remoteAddress() + " 下线");
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        // 关闭上下文  即通道
        ctx.close();
    }

}
