# Netty 项目第一版本



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



## (二) NIO 线程模型

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





## (五)  Netty Http服务



#### 【Http简述】

应用层协议，默认是80端口，最早推出1991年。

```
Get  /index.html

<html>
   <body> hello world</body>
</html>
```



五年后，1.0版本发布，不只文本可以发送，任何格式内容都支持。
提供了POST和HEAD，以及更改了请求和响应的格式，增加了头信息。

##### 【content-type字段】

声明数据格式及其编码 （服务端声明给客户端使用的）
text/html   text/plain  
image/png  ....
application/javascript

```
Content-Type: text/html; charset=utf-8
```

一级类型 和  二级类型



##### 【Accept字段】

客户端声明可以接收的数据格式

```
Accept: */*
```



##### 【Accept-Encoding字段】

```
Accept-Encoding: gzip,deflate
```

客户端可以接收哪些压缩方法

对应Content-Encoding



此时，每个TCP连接只能发送一次请求，发送完成即关闭，性能较差。

为解决1.0版本，对TCP连接的使用成本过高问题，推出Connection。

##### 【Connection字段】

```
Connection: keep-alive
```



再两年后，推出http/1.1版本

1）引入持久连接的功能，TCP连接默认不关闭，此时无需声明Connection。

2）引入管道机制，在同一个TCP连接里，客户端可以发送多个请求，服务端仍然按照顺序处理和响应，管道管理的是请求的处理逻辑/顺序。

3）增加【content-length字段】声明数据的长度

4）新增 PUT 、DELETE、PATCH、OPTIONS



此版本的缺点：数据按顺序进行，当有前面的响应很慢的时候，出现阻塞，这个现象叫做“队头阻塞”



#### 【Http组成】

请求头、请求数据、数据尾部信息

HttpRequest    HttpContent    LastHttpContent

-》  FullHttpRequest   代表完整的http请求   



Netty提供的关于http的handler：

HttpResponseDecoder  解码器，处理服务端的响应（客户端）
HttpRequestEncoder 编码器，处理服务端的请求（客户端）
HttpRequestDecoder  解码器，处理客户端的请求（服务端）
HttpResponseEncoder  编码器，处理客户端的响应（服务端）

HttpClientCodeC : 编码解码器，用于客户端 HttpResponseDecoder + HttpRequestEncoder 
HttpServerCodeC:  编码解码器，用于服务端 HttpRequestDecoder + HttpResponseEncoder

由于http的请求和响应，可能由很多部分组成，需要聚合成一个完整的消息
HttpObjectAggregator   ->  FullHttpRequest / FullHttpResponse

压缩数据的使用
HttpContentCompressor   压缩，用于服务端
HttpContentDeCompressor   解压缩，用于客户端



#### 【Http Demo 处理逻辑】

1） 创建server
2） 创建初始化器，复习了netty提供的http编码解码器、压缩器、聚合器等等，此时泛型使用Channel
3） 创建handler，泛型使用FullHttpRequest
     a)   新建响应DefaultFullHttpResponse，设定三大参数，分别为http版本、响应码、响应数据
     b）使用HttpHeaders 设置请求头，此时HttpHeaderNames 提供了设置请求头的字段，HttpHeaderValues  提供了请求头字段的常用参数，不要忘记设置长度
     c） read0方法中使用 write 方法 ， 在readComplete  中使用flush
4） 通过浏览器或postman验证 







## (六) WebSocket



#### 【初识】

http协议的缺陷： 通信只能由客户端发起。

需要一种服务端能够主动推送的能力，websocket。

这种双向通信的能力，也叫“全双工” 



<img src="images/image-20200426210537562.png" alt="image-20200426210537562" style="zoom:80%;" />

协议标识符：   http://127.0.0.1/    ->    ws://127.0.0.1/
支持文本和二进制的数据传输，可以和任意服务器通信。
http协议，是请求和响应，websocket是先握手建立连接，然后一直使用此链接，最终关闭。大大减少通信过程中数据传输的大小，以及频繁创建连接的资源消耗。
是HTML5提出的，让浏览器和服务器通信的方式。

通信的最小单位是帧frame
发送端： 将消息切割成多个帧，发送给服务端
接收端： 接收消息帧，然后将关联的帧进行重新组装，拿到完整的消息



```
GET ws://127.0.0.1:9988  HTTP/1.1
Host: localhost
Upgrade: websocket    // 升级为ws协议的说明
Connection: Upgrade 
Sec-WebSocket-Key: client-random-string
Sec-WebSocket-Version: 13
```

Upgrade: websocket和Connection: Upgrade，标识升级信息。
后两项标识协议版本



```
Http/1.1  101  Switching Protocols
Upgrade: websocket
Connection: Upgrade 
Sec-WebSocket-Accept: server-random-string
```

响应码101，代表http协议更改为websocket协议。



WebSocket实现的本质：

TCP本身是实现了全双工通信，http的请求应答机制其实限制了这种方式，websocket在连接建立之后，不再使用http协议，以此达到互相发送数据的能力。



#### 【客户端】

WebSocket对象，以及相关的事件。

| 事件    | 方法      | 说明                 |
| ------- | --------- | -------------------- |
| open    | onopen    | 连接建立时触发       |
| close   | onclose   | 连接关闭时触发       |
| message | onmessage | 接收服务端数据时触发 |
| error   | onerror   | 发生错误时触发       |



相关方法
send()   使用连接去发送数据
close()  关闭连接

```
var ws = new WebSocket("ws://127.0.0.1");
ws.onopen = function(evt){
   console.log("connection open");
   ws.send("hello websocket");  // 发送数据的方法
};
```



WebSocket连接的状态，使用readyState来声明

CONNECTING   正在连接
OPEN   连接成功可以通信
CLOSING   正在关闭
CLOSED   连接关闭或打开连接失败 



#### 【服务端】

1）WebSocketServer，没什么变化
2）WebSocketInitializer ，除了http的编解码器和聚合器外，还增加了ChunkedWriteHandler（块方式写）和WebSocketServerProtocolHandler（升级协议使用）
3）WebSocketHandler， 继承父类的泛型为TextWebSocketFrame，是数据传输的单位，在channelRead0方法中写数据，也需要给通道传入此类型的对象。



html编写
1）两个文本框，分别用来客户端写数据和读数据（服务端返回）。
2）编写js，判断是否支持websocket，创建websocket对象，设置请求地址，声明处理事件的方法，四大事件对应四大方法
3）一个发送按钮，点击操作触发websocket的send发送数据方法，此时需要判断websocket的状态（四种状态）





## (七)  心跳检测和TCP粘包拆包



#### 【心跳检测】

检测逻辑：
1） 服务端启动，客户端建立连接，连接的目的是互相发送消息。
2） 如果客户端在工作，服务端一定能收到数据，如果客户端空闲，服务端会出现资源浪费。
3） 服务端需要一种检测机制，验证客户端的活跃状态，不活跃则关闭。



需求设计：
1） 客户端向服务端发送 “I am alive” , sleep一个随机时间，模拟空闲状态
2） 服务端收到消息后，返回“over”， 客户端有空闲，记录空闲次数
3） 设定阈值，达到阈值时主动关闭连接



IdleStateHandler , 是netty提供的处理器
1）超过多长时间没有读   readerIdleTime
2)   超过多长时间没有写   writerIdleTime
3)   超过多长时间没有读和写   allIdleTime

底层实现检测的是 IdleStateEvent事件，通过管道传递给下一个handler处理，处理方法是userEventTriggered。

其中IdleStateEvent事件，分为READER_IDLE、WRITER_IDLE、ALL_IDLE三大类



#### 【TCP粘包和拆包】

TCP是“流”协议，基于字节流，没有界限，只会根据TCP缓冲区的情况进行拆分，所以业务上完整的包，可能被拆分成多个进行发送。

UDP是基于报文的，并且首部使用16bit来指示数据报文的长度，所以不会发生拆分现象。



图解

<img src="images/image-20200427214031581.png" alt="image-20200427214031581" style="zoom:80%;" />

发送了两个数据包，服务端只收到一个，发生粘包；
即使收到了两个，如果包是不完整的，发生拆包；



发生的原因：

1） 要发送的数据 >  TCP缓冲区的剩余大小，发生拆包
2） 要发送的数据 >  最大报文长度，发生拆包
3） 要发送的数据 <<  TCP缓冲区的剩余大小， 发生粘包
4） 接收数据的应用层，没有及时读取缓冲区数据，也会发生粘包



解决办法：
1） 消息的长度上设置
2） 消息的边界上设置（分隔符）



Netty提供的解码器，分为两类：
1） 基于分隔符的协议，使用定义的字符分隔消息的开头和结尾。

​       DelimiterBasedFrameDecoder  根据用户提供的分隔符处理的解码器
​       LineBaseFrameDecoder   根据行尾符("\n" 或 "\r\n")分隔



2） 基于长度的协议，增加包头部，在头部中声明数据的长度。

​      LengthFieldBasedFrameDecoder  自定义长度的处理方式
​      FixedLengthFrameDecoder   固定长度的处理方式



#### 【Demo逻辑】

需求： 客户端循环100次向服务端请求时间
1）第一种实现方式，传输过程的数据单位是字节流ByteBuf，我们需要自行处理分隔符，以及数据长度等，此时会出现粘包和拆包的问题。
2）第二种实现方式，使用LineBaseFrameDecoder，配合StringDecoder使用，传输的数据变成了字符串，可以直接处理数据，让业务逻辑上的包和真正传输的包基本一致。



## (八) 序列化

序列化，将一个对象的状态保存起来，在需要的时候获取
主要应用于"网络传输"和“对象持久化”



java原生序列化方式的缺点：
1）无法跨语言（最为致命）
2）序列化结果（码流）很大
3）序列化耗时很长，性能低



常用的序列化框架
1）Protobuf  （Google）
2）Thrift  （Facebook）
3）Json （Gson || FastJson） 



#### 【Protobuf】

全称protocal buffers , 常用来解决rpc系统间的调用。
类似xml的生成和解析，但效率更高，生成的是字节码，可读性较差。

 

【Demo逻辑】

1）安装idea插件，protobuf support

​           如果安装之后，创建*.proto文件没有使用插件，手动设置关联关系

​           settings ->  file types  ->  找到protobuf ->  增加正则表达式

2）引入maven依赖和插件

```
<properties>
    <os.detected.classifier>windows-x86_64</os.detected.classifier>
</properties>

<build>
        <plugins>
            <plugin>
                <groupId>org.xolstice.maven.plugins</groupId>
                <artifactId>protobuf-maven-plugin</artifactId>
                <version>0.5.0</version>
                <configuration>
                    <protocArtifact>
                        com.google.protobuf:protoc:3.1.0:exe:${os.detected.classifier}
                    </protocArtifact>
                    <pluginId>grpc-java</pluginId>
                </configuration>
                <executions>
                    <execution>
                        <goals>
                            <goal>compile</goal>
                            <goal>compile-custom</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
```

3）在右侧maven project中可以找到相应的插件 (没有的话刷新)
![image-20200428220759271](images/image-20200428220759271.png)

4）在和java平级的目录下，创建proto文件夹，然后创建person.proto文件
5）person.proto

```
// 声明包名称的空间
syntax="proto3";
// 具体的类生成目录
option java_package="com.duing";
// 具体的类名
option java_outer_classname="PersonModel";

// 类结构
message Person{
    int32 id = 1;    // 此处的1代表顺序
    string name = 2;
}
```

6)   使用插件进行编译，将编译生成的代码拷贝到需要的目录下
7）编写测例进行序列化和反序列化操作





【websocket + protobuf  demo】
下载地址   https://github.com/lianggzone/netty-websocket-demo 

## (九) RPC

远程过程调用  Remote Procedure  Call， 本质是一种通信协议。
解决的问题是，不同系统通信时，能够像调用本地服务一样，调用远程服务。

本地函数调用 -》  socket通信  -》  RPC框架的封装     

1）本地调用
输入参数和输出结果，都在一个进程空间。

```java
public String sayHello(String msg){
    return "hello, "+ msg;
}
```

2）socket通信
调用方 [进程A]  ->  [方法Function]   -> [进程B] 方法实现逻辑

解决方案：  定义通信协议，传参，将处理结果返回

3）RPC框架

解决不同进程间通信的问题

![image-20200429210750226](images/image-20200429210750226.png)



使用的注意事项：
 a)   调用方调用本地函数，传入对应参数
 b)   RPC框架通过动态代理的方式，在运行时动态创建新的类（代理类），在代理类中实现通信的细节（序列化、协议格式、参数校验等），处理之后进行网络传输，服务端收到请求后，继续进行解码和逻辑处理。                          

​        需要重点关注的内容： 代理、通讯协议、序列化、网络传输
​        比如： dubbo的底层使用了netty



<img src="images/image-20200429211850450.png" alt="image-20200429211850450" style="zoom:80%;" />



客户端调用方法时，底层的逻辑，是通过动态代理的方式获取对应service，然后调用方法，实际的代理类中封装了 netty客户端的通信， nettyclient会进行相应的初始化操作，然后将协议+传参等数据，发送的nettyserver进行处理，收到返回结果后，再返回给动态代理方法的返回值。



具体的实现流程：

<img src="images/image-20200429213839623.png" alt="image-20200429213839623" style="zoom:80%;" />



进程A要等待进程B的返回结果，使用了Callable，使用线程池来管理。
       先将请求参数，通过通道传输给 进程B  去处理，wait()等待， 进程B返回结果会在channelRead方法中保存，notify()唤起，返回保存结果给线程池，再返回给代理类执行的方法的结果。









【复杂的rpc  demo】


下载地址： https://github.com/pjmike/springboot-rpc-demo 



















