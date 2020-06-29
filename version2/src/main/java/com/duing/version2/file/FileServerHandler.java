package com.duing.version2.file;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import java.io.File;
import java.net.URLDecoder;

// 泛型 用来筛选数据类型
// FullHttpRequest是完整的http请求
public class FileServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request)
            throws Exception {

        // 根据请求的地址  读取目录的文件列表  进行展示
        String uri = request.uri();
        uri = URLDecoder.decode(uri, "UTF-8");
        uri = uri.replace("/", File.separator);

        // 返回项目根目录 + uri所对应的地址  = 要展示的文件目录地址
        // 如 uri= target   返回d:/ideaSource/netty-duing-practise/target
        String path = System.getProperty("user.dir") + File.separator + uri;
        System.out.println("path=" + path);

        // 应该判断路径的有效性

        // 创建响应 返回列表
        FullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        // 不增加设置  会出现乱码
        response.headers().set(HttpHeaderNames.CONTENT_TYPE,"text/html;charset=UTF-8");

        // 确定返回的数据
        String data = fileList(path);
        // 装载到bytebuf中
        ByteBuf buf = Unpooled.copiedBuffer(data, CharsetUtil.UTF_8);
        response.content().writeBytes(buf);
        // 释放资源
        buf.release();

        // 建议增加addListener方法  监听 异步方法执行的状态
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
//        super.exceptionCaught(ctx, cause);
        // 出现异常的处理逻辑
    }


    // 文件夹的遍历
    public String fileList(String path) {
        File file = new File(path);
        // 组装html页面
        StringBuilder builder = new StringBuilder();
        builder.append("<!DOCTYPE html>\r\n");
        builder.append("<html><head><title>");
        builder.append("http文件服务");
        builder.append("</title></head><body>\r\n");

        builder.append("<h3>");
        builder.append(path).append(" 目录");
        builder.append("</h3>\r\n");
        builder.append("<ul><li>链接：<a href=\"../\">..</a></li>\r\n");

        for (File f : file.listFiles()) {
            String name = f.getName();
            builder.append("<li>链接：<a href=\"");
            builder.append(name);
            builder.append("\">");
            builder.append(name);
            builder.append("</a></li>\r\n");
        }
        builder.append("</ul></body></html>\r\n");

        return builder.toString();
    }
}
