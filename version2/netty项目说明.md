# Netty项目

### 1、整合springboot的流程

A  创建一个springboot项目、引入相关依赖
B  创建netty server和handler

​    server和handler都作为spring的组件使用   @Component
​    Server代码中，拆分启动和销毁的逻辑，start(port)和destory()
​    Handler代码中，可以引入模板引擎进行html的渲染，html还是在templates目录创建，使用thymeleaf语法

C  编写html页面
D  在配置文件中配置端口号  使用@Value获取 
E  改造主程序入口 

​    CommandLineRunner  ->  启动服务
​    Runtime ->  关闭服务    





### 2、模拟Redis的客户端

Redis的通信，需要遵循RESP协议

```
set hello 123

*3\r\n$3\r\nset\r\nhello\r\n123
```



建立channel通道后，发送命令给服务端，此时是写数据【输出】，在出站handler里增加逻辑；

当接收到响应后，此时需要读数据【输入】，在入栈handler里增加逻辑；



Redis命令对应的回复类型，每种类型对应的第一个字节都不相同

1）单行回复
2）错误消息
3）整型数字
4）批量回复
5）多个批量回复



### 3、Redis整合SpringBoot



常用客户端：jedis 、Redisson、Lettuce

