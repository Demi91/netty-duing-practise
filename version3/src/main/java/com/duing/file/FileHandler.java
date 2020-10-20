package com.duing.file;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import java.io.File;
import java.net.URLDecoder;

// 只处理http请求  通过泛型进行筛选
//  FullHttpRequest 完整的http请求
//  SimpleChannelInboundHandler是ChannelInboundHandlerAdapter的子类
//  channelRead0()是对原channelRead()方法的封装  增加自动释放资源的逻辑
public class FileHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    // 读取数据的具体逻辑
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg)
            throws Exception {


        // 支持访问具体文件夹
        String uri = msg.uri();
        uri = URLDecoder.decode(uri,"UTF-8");

        // 接收请求后  返回当前项目根目录下的文件列表
        // D:\ideaSource\netty-duing-practise\target
        String path = System.getProperty("user.dir") + File.separator + uri;
        System.out.println("path==" + path);

        // 如果是文件 可以拦截  不返回

        // 遍历所有文件  返回
        String data = fileList(path);

        FullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        response.headers().set(HttpHeaderNames.CONTENT_TYPE,"text/html;charset=UTF-8");

        ByteBuf buf = Unpooled.copiedBuffer(data, CharsetUtil.UTF_8);
        response.content().writeBytes(buf);

        // 增加监听方法  监听异步关闭的事件
        ctx.writeAndFlush(response).addListeners(ChannelFutureListener.CLOSE);

    }


    // 对文件夹进行遍历
    public String fileList(String path){
        File file = new File(path);

        StringBuilder builder = new StringBuilder();
        builder.append("<html><head><title>");
        builder.append("http文件服务");
        builder.append("</title></head> <body>\r\n");

        builder.append("<h3>");
        builder.append(path).append(" 目录");
        builder.append("</h3>");

        builder.append("<ul><li> 链接：<a href=\"../\"> .. </a></li> \r\n");

        // 组装html页面进行返回
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
