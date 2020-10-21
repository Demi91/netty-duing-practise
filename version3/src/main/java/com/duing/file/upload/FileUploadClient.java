package com.duing.file.upload;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

import java.io.File;

public class FileUploadClient {

    public static void main(String[] args) {

        // 指定文件   构造Entity对象
        String fileName = "D:\\ideaSource\\netty-duing-practise\\version3\\readme.txt";
        FileUploadEntity entity = new FileUploadEntity();
        File file = new File(fileName);

        entity.setFile(file);
        entity.setFileName(file.getName());
        entity.setFileSize((int) file.length());


        EventLoopGroup group = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ch.pipeline().addLast(new ObjectEncoder());
                        ch.pipeline().addLast(new ObjectDecoder(
                                Integer.MAX_VALUE,
                                // ClassResolvers 代表会去加载已序列化的对象
                                ClassResolvers.softCachingConcurrentResolver(null)
                        ));

                        // 自定义的处理器
                        ch.pipeline().addLast(new FileUploadClientHandler(entity));
                    }
                });

        System.out.println("客户端初始化完成");
        try {
            ChannelFuture future = bootstrap.connect("127.0.0.1", 8765).sync();
            future.channel().closeFuture().sync();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }

    }
}
