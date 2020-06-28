package com.duing.version1.heartbeat;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;

public class HeartBeatHandler extends SimpleChannelInboundHandler<String> {

    // 读空闲的次数
    int readIdleTimes = 0;

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
//        super.userEventTriggered(ctx, evt);

        IdleStateEvent event = (IdleStateEvent) evt;

        String type = "";
        switch (event.state()) {
            case READER_IDLE:
                type = "读空闲";
                readIdleTimes++;
                break;
            case WRITER_IDLE:
                type = "写空闲";
                break;
            case ALL_IDLE:
                type = "读写空闲";
                break;
        }

        System.out.println(ctx.channel().remoteAddress() + "发生超时事件：" + type);

        if(readIdleTimes > 3){
            System.out.println("读空闲超过3次，关闭连接");
            ctx.channel().writeAndFlush("you are out");
            ctx.channel().close();
        }

    }


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
//        super.channelActive(ctx);
        System.out.println(ctx.channel().remoteAddress() + " is alive====");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        System.out.println("received message: " + msg);

        if("I am alive".equals(msg)){
            ctx.channel().writeAndFlush("over");
        }
    }
}
