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



### 4、IM(即时通讯系统)

复用web-im开源项目的前端代码 
地址：https://github.com/javanf/web-im

使用时  安装node环境  启动服务端
通过ide启动客户端   查看demo

客户端的唯一改动，是 src/App.vue  里面WebSocket的连接地址



1）将websocket demo整合进Springboot中，确保客户端和服务端的正常通信
2）分析数据结构，根据不同的逻辑返回对应的数据

   “数据是启动项目的第一步”

​    当前Demo功能分析：
​    A)   创建昵称登录，代表用户上线，广播给其他用户
​    B)   登录后，可以查看其它在线用户，和已存在的群组。
​    C） 可以和其他用户一对一聊天
​    D)   可以创建群组，或者加入群组，然后发送消息，可以一对多聊天

​    按照处理方式的不同，
​    可以分为操作类别（操作群组、操作用户等等）、消息类别（一对一、一对多聊天）

​    按照请求的具体逻辑划分
​    可以分为 [用户登录]（创建连接）、[用户注销]（断开连接）
​                    [创建群组]、[加入群组]
​                    [发送消息]   （消息内部来划分，私聊或群聊）

   //  这里的设计可以改进为   发送私聊&发送群聊   是不同的类别

​    

​    数据模型设计：
​    A)   用户 ：  昵称 nickname、UID 
​    B）群组：   群组ID、群组名称name、用户列表
​    C)   消息 (可以设计单独模型)

​            bridge[uid, otherUid]     不为空，代表是一对一的消息，uid发送给otherUid的消息
​            bridge[]  为空，代表是一对多的消息，此时数据增加一个群组ID—groupId

​     增加了type类型，分别为  1 创建连接、2 断开连接、10 创建群、 20 加入群、100 发消息
​     在客户端请求时，type对应如上类别，在服务端响应时，type又对应1 操作 和 2 消息两大类型

   

​    接口设计

   1）用户创建

```
{"uid":"web_im_1593668895325","type":1,"nickname":"123","bridge":[],"groupId":""}
```

```
{"type":1,"date":"2020-07-02 14:12:23","msg":"123加入聊天室","users":[{"nickname":"123","uid":"web_im_1593668895325","status":1},{"nickname":"554","uid":"web_im_1593668994253","status":1}],"groups":[{"id":1593668901314,"name":"222","users":[{"uid":"web_im_1593668895325","nickname":"123"},{"uid":"web_im_1593668994253","nickname":"554"}]}],"uid":"web_im_1593668895325","nickname":"123","bridge":[]}
```

2)   创建群组

```
{"uid":"web_im_1593668895325","type":10,"nickname":"123","groupName":"newGroup","bridge":[]}
```

```
{"type":1,"date":"2020-07-02 14:13:00","msg":"123创建了群newGroup","users":[{"nickname":"123","uid":"web_im_1593668895325","status":1},{"nickname":"554","uid":"web_im_1593668994253","status":1}],"groups":[{"id":1593668901314,"name":"222","users":[{"uid":"web_im_1593668895325","nickname":"123"},{"uid":"web_im_1593668994253","nickname":"554"}]},{"id":1593670380993,"name":"newGroup","users":[{"uid":"web_im_1593668895325","nickname":"123"}]}],"uid":"web_im_1593668895325","nickname":"123","bridge":[]}
```

3)  有人退出聊天室

```
{"type":1,"date":"2020-07-02 14:13:57","msg":"554退出了聊天室","users":[{"nickname":"123","uid":"web_im_1593668895325","status":1},{"nickname":"554","uid":"web_im_1593668994253","status":0}],"groups":[{"id":1593668901314,"name":"222","users":[{"uid":"web_im_1593668895325","nickname":"123"},{"uid":"web_im_1593668994253","nickname":"554"}]},{"id":1593670380993,"name":"newGroup","users":[{"uid":"web_im_1593668895325","nickname":"123"}]}],"uid":"web_im_1593668994253","nickname":"554","bridge":[]}
```

4)  加入群组

```
{"uid":"web_im_1593668895325","type":20,"nickname":"123","groupId":1593670445119,"groupName":"1234","bridge":[]}
```

```
{"type":1,"date":"2020-07-02 14:14:46","msg":"123加入了群1234","users":[{"nickname":"123","uid":"web_im_1593668895325","status":1},{"nickname":"554","uid":"web_im_1593668994253","status":1}],"groups":[{"id":1593668901314,"name":"222","users":[{"uid":"web_im_1593668895325","nickname":"123"},{"uid":"web_im_1593668994253","nickname":"554"}]},{"id":1593670380993,"name":"newGroup","users":[{"uid":"web_im_1593668895325","nickname":"123"}]},{"id":1593670445119,"name":"1234","users":[{"uid":"web_im_1593668994253","nickname":"554"},{"uid":"web_im_1593668895325","nickname":"123"}]}],"uid":"web_im_1593668895325","nickname":"123","bridge":[]}
```

5)  一对一聊天

```
{"uid":"web_im_1593668895325","type":100,"nickname":"123","msg":"hello 554","bridge":["web_im_1593668895325","web_im_1593668994253"],"groupId":""}
```

```
{"type":2,"date":"2020-07-02 14:15:09","msg":"hello 554","uid":"web_im_1593668895325","nickname":"123","bridge":["web_im_1593668895325","web_im_1593668994253"],"groupId":"","status":1}
```

6)   一对多聊天

```
{"type":2,"date":"2020-07-02 14:13:06","msg":"123","uid":"web_im_1593668895325","nickname":"123","bridge":[],"groupId":1593670380993,"status":1}
```



可以改进的点

使用bridge作为一对一或一对多的类型判断，有些繁琐  可以增加type类型来处理，如type=200 （一对多），type=100（一对一）

对于用户信息的处理，可以简化

数据结构不够明确





### 5、UDP

传输层的协议   User Datagram Protocal

基于报文传输的



| 分类     | TCP                           | UDP                                      |
| -------- | ----------------------------- | ---------------------------------------- |
|          | 面向连接                      | 无连接                                   |
|          | 只有两端，只能一对一通信      | 可以一对一、一对多、多对一、多对多的通信 |
|          | 基于字节流                    | 基于报文                                 |
| 重要特性 | 可靠                          | 不可靠（尽最大努力交付）                 |
|          | 首部占用空间大，20-60字节之间 | 首部占用空间小，8字节（记录报文长度）    |



UDP的分类：

单播、多播（组播）、广播

单播：一对一
组播：一对多（逻辑上的分组）
广播：一对多（局域网内的广而告之）



原生UDP的实现
1）DatagramSocket代表通信的一端
2）DatagramPacket是数据的通信格式，报文
           在创建时，需要明确的是，数据的字节数组，以及另一端的ip地址+端口
           在接收报文和发送报文前，使用字节数组进行接收和组装
3） socket去接收和发送时，对应receive()和send()方法



通过Netty来实现
1）DatagramSocket 对应 NioDatagramChannel
2）java.net.DatagramPacket 对应 import io.netty.channel.socket.DatagramPacket;
3）无论客户端还是服务端都使用Bootstrap启动
4）通过调用Bootstrap的localAddress()指定端口号，也可以调用remoteAddress()指定连接地址（ip+port）
5)  自定义handlder的使用，继承自SimpleChannelInboundHandler之外，泛型被声明为DatagramPacket，重要逻辑仍然在channelRead0()之中
6）组装DatagramPacket，通过ByteBuf，加上SocketAddress（ip地址+端口）
      组装ByteBuf，调用Unpooled工具类的copiedBuffer()方法，明确字符串和编码格式。





作业：
1） 通过netty实现http服务，以及websocket服务
2） 基于已有的Springboot+Redis封装，存储业务数据到redis中
           比如用户登录后的session，或者增加好友申请，群组申请等功能，二维码扫描等













