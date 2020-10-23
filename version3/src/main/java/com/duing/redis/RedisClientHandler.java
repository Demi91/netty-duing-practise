package com.duing.redis;

import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.redis.*;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;

import java.util.ArrayList;
import java.util.List;

// 使用了 第三种  自定义处理器的方式   继承ChannelDuplexHandler
//  InBound  OutBound   入站和出站操作

//  客户端 《=》 服务端   需要进站来进行处理的请求  需要经过所有 InBoundHandler  读取
//     要返回数据时   需要先处理请求后出站  需要经过所有 OutBoundHandler  写入
//   同时涉及两方操作  使用双重/复合 处理器
public class RedisClientHandler extends ChannelDuplexHandler {


    @Override
    public void write(ChannelHandlerContext ctx, Object msg,
                      ChannelPromise promise) throws Exception {
//        super.write(ctx, msg, promise);
        // 因为命令  是按照空格分隔  所以需要处理
        // ping -> pong   set hello 123 -> ok   get hello -> 123
        String[] commands = ((String) msg).split("\\s+");
        List<RedisMessage> list = new ArrayList<>();
        for (String cmd : commands) {
            // 将ByteBuf 封装为 FullBulkStringRedisMessage （批量回复类型的数据格式）
            list.add(new FullBulkStringRedisMessage(
                    // 将字符串 转化为ByteBuf
                    ByteBufUtil.writeUtf8(ctx.alloc(), cmd)
            ));
        }

        // 再将多个 FullBulkStringRedisMessage  封装为数组类型 （多个批量回复的格式）
        RedisMessage request = new ArrayRedisMessage(list);
        ctx.write(request, promise);

    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        RedisMessage redisMessage = (RedisMessage) msg;
        printRedisMsg(redisMessage);

        // 要自行释放资源
        ReferenceCountUtil.release(redisMessage);

        // 根据定义的格式  读取具体的返回结果
//        super.channelRead(ctx, msg);
    }

    // 判断类型并处理
    private void printRedisMsg(RedisMessage msg) {
        if (msg instanceof SimpleStringRedisMessage) {
            SimpleStringRedisMessage tmp = (SimpleStringRedisMessage) msg;
            System.out.println(tmp.content());
        } else if (msg instanceof ErrorRedisMessage) {
            ErrorRedisMessage tmp = (ErrorRedisMessage) msg;
            System.out.println(tmp.content());
        } else if (msg instanceof IntegerRedisMessage) {
            IntegerRedisMessage tmp = (IntegerRedisMessage) msg;
            System.out.println(tmp.value());
        } else if (msg instanceof FullBulkStringRedisMessage) {
            FullBulkStringRedisMessage tmp = (FullBulkStringRedisMessage) msg;
            String msgResult = tmp.content().toString(CharsetUtil.UTF_8);
            System.out.println(msgResult);
        } else if (msg instanceof ArrayRedisMessage) {
            ArrayRedisMessage tmp = (ArrayRedisMessage) msg;
            for (RedisMessage child : tmp.children()) {
                printRedisMsg(child);
            }
        }
    }
}
