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



#### 4、IM(即时通讯系统)

复用web-im开源项目的前端代码 
地址：https://github.com/javanf/web-im

使用时  安装node环境  启动服务端
通过ide启动客户端   查看demo

客户端的唯一改动，是 src/App.vue  里面WebSocket的链接地址



1）将websocket demo整合进Springboot中，确保客户端和服务端的正常通信
2）分析数据结构，根据不同的逻辑返回对应的数据

   “数据是启动项目的第一步”

​    当前Demo功能分析：
​    A)   创建昵称登录，代表用户上线，广播给其他用户
​    B)   登录后，可以查看其它在线用户，和已存在的群组。
​    C）可以和其他用户一对一聊天
​    D)  可以创建群组，或者加入群组，然后发送消息，可以一对多聊天


​    按照处理方式的不同，
​    可以分为操作类别（操作群组、操作用户等等）、消息类别（一对一、一对多聊天）

​    按照请求的具体逻辑划分
​    可以分为 [用户登录]（创建连接）、[用户注销]（断开连接）
​                    [创建群组]、[加入群组]
​                    [发送消息]   （消息内部来划分，私聊或群聊）

​    

​    数据模型设计：
​    A)   用户 ：  昵称 nickname、UID 
​    B）群组：   群组ID、群组名称name、用户列表
​    C)   消息 (可以设计单独模型)

​            bridge[uid, otherUid]     不为空，代表是一对一的消息，uid发送给otherUid的消息
​            bridge[]  为空，代表是一对多的消息，此时数据增加一个群组ID—groupId

​     增加了type类型，分别为  1 创建连接、2 断开连接、10 创建群、 20 加入群、100 发消息
​     在客户端请求时，type对应如上类别，在服务端响应时，type又对应1 操作 和 2 消息两大类型

   

​    接口设计：

   