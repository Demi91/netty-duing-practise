package com.duing.file.upload;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.io.IOException;
import java.io.RandomAccessFile;

public class FileUploadClientHandler
        extends SimpleChannelInboundHandler<FileUploadEntity> {


    private FileUploadEntity entity;
    private RandomAccessFile file;

    // 需要使用的属性或参数
    private int dataNum = 10;
    // 每一段数据的长度
    private int onceDataLength = 0;


    // 数据读取的起始位置
    private int start = 0;
    // 需读取数据的长度
    private int dataLength = 0;


    // 客户端和服务端交互的次数
    private int times = 0;


    public FileUploadClientHandler(FileUploadEntity entity) throws IOException {
        this.entity = entity;
        this.file = new RandomAccessFile(entity.getFile(), "r");
        this.onceDataLength = (int) file.length() / dataNum;
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FileUploadEntity msg)
            throws Exception {

    }


    // 初始化通道后  第一次建立连接会调用
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
//        super.channelActive(ctx);

        System.out.println("调用channelActive === ");

        // 起始位置
        file.seek(0);
        dataLength = onceDataLength;
        upload(ctx);

        System.out.println("第" + times + "次上传文件");
    }

    // 同时重写 channelRead 和 channelRead0  默认执行前者
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)
            throws Exception {

//        super.channelRead(ctx, msg);

        // 服务端返回  下一次上传的起始位置
        if (msg instanceof Integer) {
            start = (Integer) msg;
            System.out.println("服务端返回  下一次上传的起始位置 start == " + start);

            if (start == -1) {
                file.close();
                ctx.close();
                System.out.println("文件传输完成");
                return;
            }
        }

        file.seek(start);
        int leaveDataLength = (int) (file.length() - start);
        // 比如 总125  125/10 = 12   0-12 12-24 24-36 ...  108-120  120-125 (共11次)
        if (leaveDataLength < onceDataLength) {
            dataLength = leaveDataLength;
        }

        // 上传
        upload(ctx);

    }

    private void upload(ChannelHandlerContext ctx) throws IOException {

        byte[] bytes = new byte[dataLength];
        // 本次读取
        int byteRead = 0;
        if ((byteRead = file.read(bytes)) != -1) {
            times++;

            entity.setDataLength(byteRead);
            entity.setBytes(bytes);
            ctx.writeAndFlush(entity);

            System.out.println("数据的起始位置 start = " + start
                    + ", 本次上传的长度 dataLength = " + dataLength);
        }


    }
}
