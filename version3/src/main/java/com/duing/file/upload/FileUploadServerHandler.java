package com.duing.file.upload;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.io.File;
import java.io.RandomAccessFile;

public class FileUploadServerHandler
        extends SimpleChannelInboundHandler<FileUploadEntity> {


    // 指定服务器保存文件的位置
    private String fileDir = "C:\\Users\\Cherise\\Desktop";

    // 读取到的数据长度
    private int dataLength;
    // 读取的起始位置
    private int start = 0;

    private RandomAccessFile randomAccessFile;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FileUploadEntity msg)
            throws Exception {

        FileUploadEntity entity = msg;
        byte[] bytes = entity.getBytes();
        // 计算每次接收到的数据长度
        dataLength = entity.getDataLength();
        System.out.println("dataLength == " + dataLength);

        String fileName = entity.getFileName();
        String path = fileDir + File.separator + fileName; // 文件路径

        // 读写权限  rw
        randomAccessFile = new RandomAccessFile(path,"rw");
        randomAccessFile.seek(start);
        randomAccessFile.write(bytes);
        randomAccessFile.close();

        // 变更起始位置 （下一次数据传输的起始点）
        // 返回给客户端
        start = start + dataLength;
        System.out.println("返回给客户端 start ==" + start);

        // 何时将整个文件读取完成？
        if(start == entity.getFileSize()){
            // 读取完  返回-1
            ctx.writeAndFlush(-1);
            ctx.close();
            return;
        }

        // 读取未完成
        ctx.writeAndFlush(start);

    }
}
