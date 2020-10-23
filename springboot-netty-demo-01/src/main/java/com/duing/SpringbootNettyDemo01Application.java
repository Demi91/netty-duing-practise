package com.duing;

import com.duing.file.FileServer;
import io.netty.channel.ChannelFuture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 改造主程序入口   springboot项目启动时  netty服务也启动起来
 * <p>
 * CommandLineRunner 是一个在项目启动后  指定执行某些逻辑的接口
 * 具体逻辑 在run方法中实现
 */
@SpringBootApplication
public class SpringbootNettyDemo01Application implements CommandLineRunner {

    @Value("${netty.port}")
    private int port;

    @Autowired
    private FileServer fileServer;

    public static void main(String[] args) {
        SpringApplication.run(SpringbootNettyDemo01Application.class, args);
    }


    /**
     * addShutdownHook  在jvm中增加一个关闭的钩子
     * 在jvm关闭时  先去执行 钩子中的逻辑  addShutdownHook方法中代码
     * 当执行完钩子  jvm才关闭   所以适用于解决内存清理、对象销毁等情况
     *
     * @param args
     * @throws Exception
     */
    @Override
    public void run(String... args) throws Exception {
        // 启动
        ChannelFuture future = fileServer.start(port);
        // 关闭
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                fileServer.destroy();
            }
        });

        future.channel().closeFuture().sync();

    }
}
