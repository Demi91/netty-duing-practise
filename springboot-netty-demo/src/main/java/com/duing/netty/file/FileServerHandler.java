package com.duing.netty.file;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// 泛型 用来筛选数据类型
// FullHttpRequest是完整的http请求  代表要处理的数据单位
// SimpleChannelInboundHandler 是子类 ChannelInboundHandlerAdapter
//    封装了channelRead（）方法   ByteBuf被使用时  会自动释放资源

// 因为handler被spring托管  可能会被多个通道共享  所以使用ChannelHandler.Sharable
@Component
@ChannelHandler.Sharable
public class FileServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    // channelRead0 是读取数据的方法
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
        File file = new File(path);
        if(!file.exists() || !file.isDirectory()){
            // 可以返回处理失败的响应
            return;
        }

        // 创建响应 返回列表
        FullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        // 不增加设置  会出现乱码
        response.headers().set(HttpHeaderNames.CONTENT_TYPE,"text/html;charset=UTF-8");

        // 确定返回的数据
//        String data = fileList(path);
        String data = fileListByEngine(path);
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


    @Autowired
    private TemplateEngine templateEngine;

    public String fileListByEngine(String path) {
        // 1 拿到模板引擎的对象
        // 2 获取要渲染的数据
        File file = new File(path);
        List<String> nameList = new ArrayList<>();
        for(File subFile : file.listFiles()){
            nameList.add(subFile.getName());
        }

        Context context = new Context();
        Map<String,Object> valueMap = new HashMap<>();
        valueMap.put("path",path);
        valueMap.put("nameList",nameList);
        context.setVariables(valueMap);

        // 3 编写静态页面
        // 4 拿到最终结果
        String content = templateEngine.process("fileList",context);
        return content;
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
