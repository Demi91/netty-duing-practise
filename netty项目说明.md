# Netty 项目



## (一) NIO Demo

需求分析： 群聊系统
    实现服务端和客户端之间的通讯（非阻塞）

1） 客户端发送数据，服务端能够一直接收。
2） 多个客户端发送的数据，彼此相互可见。
            服务端能够广播给其他客户端，除了发送数据的客户端之外



服务器： ServerSocketChannel
1)   监听端口，获得和客户端的连接
2）获取的是 SocketChannel， 注册给选择器，并且声明监听事件（读写）
3）在读数据后，将此数据再写入给其他客户端



## (二) NIO线程模型

Reactor模型定义
1）事件驱动
2）可以处理一个或多个数据源
3）通过多路复用将请求的事件分发给对应的处理器处理

三大核心角色：Reactor  Acceptor  Handler

Reactor：监听事件的发生，并分发给对应的handler处理，或者分发给acceptor
Acceptor： 处理客户端连接事件，并创建handler
Handler：处理后续的读写事件

#### 【单Reactor单线程模型】

<img src="images/image-20200421221449208.png" alt="image-20200421221449208" style="zoom: 67%;" />

select一直监听事件，事件发生后触发dispatch。
如果事件是建立请求的事件，又acceptor去创建handler来处理业务的
如果不是建立请求的事件，找到对应的handler处理业务。

注： redis就是这样处理的

#### 【单Reactor多线程模型】

<img src="images/image-20200421214503715.png" alt="image-20200421214503715" style="zoom: 67%;" />

和单线程模型的主要区别，是具体业务逻辑不由handler处理，handler只负责读数据，将数据传给子线程，子线程处理完再将结果返回给handler，handler再发送给客户端。



#### 【主从Reactor模型】

![image-20200421215357546](images/image-20200421215357546.png)

主reactor， 用来处理连接的请求和时间，有连接就分配给acceptor。然后分配给从reactor，让从reactor创建对应的handler，并可以监听后续的读写事件。
不是连接事件时，分配给 “从reactor” , 从reactor去查找对应的handler。



类比于餐厅的接待员和服务员

1）单Reactor单线程： 接待员和服务员是同一个人，一直服务
2）单Reactor多线程：一个接待员和多个服务员
3）主从Reactor：两个接待员和多个服务员



#### 【Netty模型】

主从Reactor对应到netty中， BossGroup 和  WorkerGroup  —>  NioEventLoopGroup
BossGroup  负责接收客户端的连接
WorkerGroup  负责网络的读写

NioEventLoopGroup 是一个事件循环组，组中包含很多个事件循环
  NioEventLoop  代表一个不断循环处理任务的线程   每一个都有selector 用于监听



BossGroup   
1)  轮询是否有accept事件发生
2）处理事件，并且和客户端建立连接， NioSocketChannel -> SocketChannel -> Socket
      注册到workergroup中，使用seletor进行后续监听
3）处理任务队列

WorkerGroup
1）轮询是否有read and write事件发生
2)   找到 NioSocketChannel
3）处理任务队列



## (三) Netty HelloWorld

### 【Demo 处理逻辑】

1)  创建group,  服务端两个，分别为boss和worker，客户端一个

2)  启动对象初始化
ServerBootstrap  和  Bootstrap
对于服务端而言，先后设置其中的线程组group、通道channel、处理器handler、客户端通道对应的处理器childHandler

其中自定义handler的设置，需要先有通道初始化器ChannelInitializer<SocketChannel>，实现其中的通道初始化方法，具体逻辑为 获取通道中的管道，然后加入handler

注：通道是建立连接的角色  管道是管理业务处理逻辑

**其中handler的逻辑如下**
a) 继承ChannelInboundHandlerAdapter, 此为netty提供的适配器
b) 重写其中的方法，channelActive 、channelRead、channelReadComplete，分别对应于通道创建、读事件发生、读事件完成三个时间点。
c)  方法的参数有一个 ChannelHandlerContext ，是处理器的上下文，除了获取通道和管道外，可以调用writeAndFlush() 直接写入数据

3）然后绑定端口号(服务端)， 或者连接指定的ip地址加端口号（客户端）
4）关闭group



### 【组件说明】

```
ChannelFuture类
```

异步的调用方式，调用者并不会立刻获得结果，Future-Listener机制，在调用完成时，通过回调callback的方式获得最终的结果。



```
Unpooled类
```

是netty提供的，用来操作缓冲区（数据容器）的工具类
copiedBuffer  可以将给定的数据和编码，返回ByteBuf对象



## (四)  Netty Demo

需求如NIO Demo, 群聊系统



![image-20200423210813919](images/image-20200423210813919.png)

Netty 提供了编码器和解码器
StringEncoder /  StringDecoder    对字符串处理
ObjectEncoder /  ObjectDecoder   对java对象处理



```
SimpleChannelInboundHandler类
```

继承于ChannelInboundHandlerAdapter, 重写了channelRead方法，需要自行实现channelRead0方法，好处之一是不用关心何时释放资源，底层做了处理。



### 【Demo编写逻辑】

Server And Client  

 1）  创建EventLoopGroup，注入ServerBootstrap中，理解其中的新参数

```
// 服务端暂时无法处理的连接会放在请求队列中
// backlog 指定了队列的大小
.option(ChannelOption.SO_BACKLOG,128)
// 设置保持连接状态
.childOption(ChannelOption.SO_KEEPALIVE,true)
```

 2） 创建通道初始化器  

​        实现初始化方法时， 增加编码解码器，以及自定义处理器。

3） 创建自定义处理器

​        继承的父类是SimpleChannelInboundHandler
​        a)  感知连接状态的变化对应的方法是  handlerAdded 、handlerRemoved
​        b)  感知通道是否是活跃状态的方法是  channelActive 、 channelInactive
​        c)  当出现异常时需要关闭上下文  exceptionCaught
​        d)  真正读数据的逻辑在  channelRead0 方法之中
​        
​        处理多个通道的方式  ChannelGroup ，本质上是set。 通过遍历set实现消息广播。
​        当通道是自身时，增加了人机对话。            

4） 客户端的区别

​        连接服务端后，保持监听，接收键盘输入后，直接写入通道。