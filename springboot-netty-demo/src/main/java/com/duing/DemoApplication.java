package com.duing;

import com.duing.netty.CommonServer;
import com.duing.netty.file.FileServerInitializer;
import com.duing.netty.websocket.WebSocketInitializer;
import io.netty.channel.ChannelFuture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


/**
 * CommandLineRunner 提供一个开放接口
 * 可以增加  需要在项目启动后  执行的逻辑  具体逻辑在run方法中实现
 */
@SpringBootApplication
public class DemoApplication implements CommandLineRunner {

    @Value("${netty.port}")
    private int port;

    @Autowired
    private CommonServer commonServer;

    @Autowired
    private FileServerInitializer initializer;

    @Autowired
    private WebSocketInitializer webSocketInitializer;


    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    /**
     * Runtime.getRuntime().addShutdownHook()
     * 意思是  在jvm中增加一个关闭的钩子
     * 当jvm关闭时  会查看是否有添加 关闭相关的钩子  也就是 是否设置addShutdownHook
     * 如果有  系统会先执行完钩子中的代码逻辑  再将jvm关闭
     * <p>
     * 所以钩子常常被用来  处理 内存清理、对象销毁等等的操作
     *
     * @param args
     * @throws Exception
     */
    @Override
    public void run(String... args) throws Exception {
        ChannelFuture future = commonServer.start(port, initializer);
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                commonServer.destroy();
            }
        });
        future.channel().closeFuture().sync();
    }
}
