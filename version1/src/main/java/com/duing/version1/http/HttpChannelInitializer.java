package com.duing.version1.http;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.*;

public class HttpChannelInitializer extends ChannelInitializer<Channel> {

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        pipeline.addLast("decoder",new HttpRequestDecoder());
        pipeline.addLast("encoder",new HttpResponseEncoder());

//        pipeline.addLast("codec",new HttpServerCodec());
        // 压缩数据
        pipeline.addLast("compressor", new HttpContentCompressor());
        // 聚合器   参数为可以处理的最大消息大小
        pipeline.addLast("aggregator",new HttpObjectAggregator(512 * 1024));

        // 自定义handler
        pipeline.addLast(new MyHttpHandler());

    }
}
