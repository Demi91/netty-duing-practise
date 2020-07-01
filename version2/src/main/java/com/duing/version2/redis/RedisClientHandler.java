package com.duing.version2.redis;

import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.redis.*;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;

import java.util.ArrayList;
import java.util.List;

// 复合的handler  既能实现入站逻辑  又能实现出站逻辑
public class RedisClientHandler extends ChannelDuplexHandler {

    // 出站handler中常用方法
    //   需要处理redis命令
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise)
            throws Exception {
        // 键盘传入的字符串  对应msg    keys *
        String commandStr = (String) msg;
        // 根据空格分离  转化成字符串数组  [keys,*]
        String[] commandArr = commandStr.split("\\s+");
        // 接收RedisMessage的列表
        List<RedisMessage> redisMessageList = new ArrayList<>(commandArr.length);
        for (String cmdStr : commandArr) {
            FullBulkStringRedisMessage message = new FullBulkStringRedisMessage(
                    // 通过ByteBufUtil工具  将string包装成ByteBuf
                    ByteBufUtil.writeUtf8(ctx.alloc(),cmdStr)
            );
            redisMessageList.add(message);
        }
        // 把存储单一message列表的值   再一次封装  封装为大的数组message  写入通道
        RedisMessage request = new ArrayRedisMessage(redisMessageList);
        ctx.write(request,promise);
//        super.write(ctx, msg, promise);
    }

    // 读取服务端返回的数据
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        super.channelRead(ctx, msg);
        RedisMessage redisMessage = (RedisMessage) msg;
        // 根据返回结果的不同类型  进行处理
        printRedisResponse(redisMessage);
        // 释放资源
        ReferenceCountUtil.release(redisMessage);
    }


    private void printRedisResponse(RedisMessage msg){
        if(msg instanceof SimpleStringRedisMessage){
            SimpleStringRedisMessage tmpMsg = (SimpleStringRedisMessage)msg;
            System.out.println(tmpMsg.content());

        }else if(msg instanceof ErrorRedisMessage){
            ErrorRedisMessage tmpMsg = (ErrorRedisMessage)msg;
            System.out.println(tmpMsg.content());

        }else if(msg instanceof IntegerRedisMessage){
            IntegerRedisMessage tmpMsg = (IntegerRedisMessage)msg;
            System.out.println(tmpMsg.value());

        }else if(msg instanceof FullBulkStringRedisMessage){
            FullBulkStringRedisMessage tmpMsg = (FullBulkStringRedisMessage)msg;
            if(tmpMsg.isNull()) return;
            System.out.println(tmpMsg.content().toString(CharsetUtil.UTF_8));

        }else if(msg instanceof ArrayRedisMessage){
            ArrayRedisMessage tmpMsg = (ArrayRedisMessage)msg;
            for(RedisMessage child : tmpMsg.children()){
                printRedisResponse(child);
            }

        }

        // 如果都不是  应该抛出异常
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
//        super.exceptionCaught(ctx, cause);
        ctx.close();
    }
}
