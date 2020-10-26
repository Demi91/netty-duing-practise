
netty项目  共10节  周期两周

java工程师能做什么？
BATZ  百度 阿里 腾讯 字节跳动

百度—— 百度搜索 百度云（搜索和存储）
阿里—— 淘宝 支付宝（电商和金融）
腾讯—— 微信 王者荣耀（社交和游戏）
字节跳动—— 今日头条  抖音（信息流）


单机游戏 —— 网络游戏  —— 网页游戏  —— 手机游戏

游戏类别不同，技术选型不同
 “长连接”  “短连接”


项目开发角色

产品经理 PD ——  项目经理（技术负责人）
  UI  前端人员  后端人员  测试人员  运维人员


游戏开发

制作人
 策划 （剧情策划  数值策划）
 美术 （2d  3d）
 前端 （h5  flash unity3d）
 后端  (c++ java python)
 测试 （黑盒  白盒）
 运维 （上线）


一些指标： 留存 日活
java技术  —— mina 网络连接  —— netty 长连接


Netty前置知识：IO —— NIO —— 网络


项目安排：

1、http的文件服务，以及文件上传

2、整合内容
  springboot
  redis

3、即时通讯工具  demo  (类似微信的样例)

4、tcp /  udp

5、知识点回顾






Day2  文件列表（http服务）
Day3  文件上传


实现思路：
分段上传
简单的方式——分割成固定的段数  

如 文件总长度125  分成10段   
   每一段是12  最后一段是5  用11次传递完成 

如何知道下一次的传递起始位置？ 何时终止？


分段传递的逻辑确定后，如何随机读取文件

RandomAccessFile 支持随机访问文件
可以指定位置读文件  也支持追加文件内容   还支持断点续传


有两个构造函数   除了指定文件外  要指定使用方式（“只读”/“读写”）

seek(long pos)  可以将文件指针 定位到pos的位置
getFilePointer()  获取指针的位置


Dat4 手写Redis客户端

 192.168.1.12  6379

 遵循服务端的数据格式（RESP协议）  进行交互


 redis服务端的数据格式 （回复类型） 分为五大格式：
 1）用单行回复  "+"   Simple
 2）错误消息   "-"   Error
 3）整型数字   ":"   Integer
 4）批量回复   "$"   BulkString
 5）多个批量回复  "*"   Array

 通过在数据的开头增加不同的标识来区分


 当使用jar包  连接redis服务端时   jar中封装的客户端代码和此代码相通


 springboot和netty的整合

 1）创建springboot项目
 2）引入netty依赖
 3）将netty的server和handler代码进行改造
      server的启动和销毁服务的操作要分开
      server对象和handlder交由spring容器管理 （增加@Component注解）  
       handler托管后，还需设置为共享的 Sharable注解
 4）springboot的启动过程中，加入对netty服务的启动和销毁
      通过实现CommandLineRunner接口的run方法，进行启动
      通过实现Runtime的addShutdownHook方法，进行销毁



springboot和redis的整合

redis的客户端分为  Jedis  Redission  Lettuce

Jedis 最早期普及的客户端  springboot1.0版本时使用
Redission  支持分布式、支持扩展的数据结构
Lettuce  更高级的客户端  springboot2.0版本后使用

  能够更好支持  线程安全  异步  集群  哨兵等高级功能


1) 引入redis依赖
2）增加redis配置
3）创建对应的配置类加载bean
4) 使用bean  在自定义的Redis工具类中



================================================

小项目   即时通讯工具

群聊系统   借鉴开源项目的前端代码
  springboot + netty + websocket  (redis之中)


参考自github上的开源项目
https://github.com/javanf/web-im

使用其前端代码，重写其后端逻辑


当前Demo功能分析：
A) 创建昵称登录，代表新用户上线，广播给其他用户
B) 登录后，查看其他在线用户，以及已存在的群组
C) 可以支持一对一聊天
D) 可以创建群组，或者加入某群组，然后发送消息，实现一对多聊天


熟悉一下项目

运行客户端
首先要执行 npm install
每次运行  执行 npm run dev


运行服务端
server/index.js
node index.js


分析数据结构

数据模型的设计：
“数据是启动项目的第一步”


按照处理方式的不同
可以分为 操作类别（操作用户、操作群组）
消息类别（一对一、一对多聊天）


按照请求的逻辑划分
[用户登录](创建连接)、[用户注销](断开连接)
[创建群组]、[加入群组]
[发送消息](内部划分为 私聊 或 群聊)

// 也可以设计为 发送私聊 & 发送群聊  =》 更推荐的设计方式

数据模型：
A) 用户   昵称nickname  UID唯一标识
B) 群组   群组名称name  群组ID  用户列表
C) 消息

   使用bridge数组  区分是私聊还是群聊
   bridge[uid,anotherUid]   不为空  是私聊
          是由uid 发送给 anotherUid的消息
   bridge[]  为空  是群聊  增加一个群组ID  groupId

使用type进行请求类型的划分 （请求消息）
1 创建连接  2 断开连接  10 创建群组  20 加入群组  
100 发送消息

服务端处理完数据后，返回给客户端的响应，也划分了类型，
同样使用了type  （响应消息）
1 操作  2 消息


接口设计
1）用户创建
请求
{"uid":"web_im_1593668895325","type":1,"nickname":"123","bridge":[],"groupId":""}
响应
{"type":1,"date":"2020-07-02 14:12:23","msg":"123加入聊天室","users":[{"nickname":"123","uid":"web_im_1593668895325","status":1},{"nickname":"554","uid":"web_im_1593668994253","status":1}],"groups":[{"id":1593668901314,"name":"222","users":[{"uid":"web_im_1593668895325","nickname":"123"},{"uid":"web_im_1593668994253","nickname":"554"}]}],"uid":"web_im_1593668895325","nickname":"123","bridge":[]}

其中uid 是由客户端生成的（实际开发中，应该由服务端生成）
   生成逻辑是  项目名web_im + 时间戳


2）创建群组   
请求
{"uid":"web_im_1593668895325","type":10,"nickname":"123","groupName":"newGroup","bridge":[]}
响应
{"type":1,"date":"2020-07-02 14:13:00","msg":"123创建了群newGroup","users":[{"nickname":"123","uid":"web_im_1593668895325","status":1},{"nickname":"554","uid":"web_im_1593668994253","status":1}],"groups":[{"id":1593668901314,"name":"222","users":[{"uid":"web_im_1593668895325","nickname":"123"},{"uid":"web_im_1593668994253","nickname":"554"}]},{"id":1593670380993,"name":"newGroup","users":[{"uid":"web_im_1593668895325","nickname":"123"}]}],"uid":"web_im_1593668895325","nickname":"123","bridge":[]}

此时影响的是群组列表  参数是groups
    创建群组的用户  自动加入群组中


3）发送群聊消息
请求
{"uid":"web_im_1593668895325","type":100,"nickname":"123","msg":"123","bridge":[],"groupId":1593670380993}  
响应
{"type":2,"date":"2020-07-02 14:13:06","msg":"123","uid":"web_im_1593668895325","nickname":"123","bridge":[],"groupId":1593670380993,"status":1}  


接收推送的响应
{"type":1,"date":"2020-07-02 14:14:05","msg":"554创建了群1234","users":[{"nickname":"123","uid":"web_im_1593668895325","status":1},{"nickname":"554","uid":"web_im_1593668994253","status":1}],"groups":[{"id":1593668901314,"name":"222","users":[{"uid":"web_im_1593668895325","nickname":"123"},{"uid":"web_im_1593668994253","nickname":"554"}]},{"id":1593670380993,"name":"newGroup","users":[{"uid":"web_im_1593668895325","nickname":"123"}]},{"id":1593670445119,"name":"1234","users":[{"uid":"web_im_1593668994253","nickname":"554"}]}],"uid":"web_im_1593668994253","nickname":"554","bridge":[]}

4）加入群组
请求
{"uid":"web_im_1593668895325","type":20,"nickname":"123","groupId":1593670445119,"groupName":"1234","bridge":[]}
响应
{"type":1,"date":"2020-07-02 14:14:46","msg":"123加入了群1234","users":[{"nickname":"123","uid":"web_im_1593668895325","status":1},{"nickname":"554","uid":"web_im_1593668994253","status":1}],"groups":[{"id":1593668901314,"name":"222","users":[{"uid":"web_im_1593668895325","nickname":"123"},{"uid":"web_im_1593668994253","nickname":"554"}]},{"id":1593670380993,"name":"newGroup","users":[{"uid":"web_im_1593668895325","nickname":"123"}]},{"id":1593670445119,"name":"1234","users":[{"uid":"web_im_1593668994253","nickname":"554"},{"uid":"web_im_1593668895325","nickname":"123"}]}],"uid":"web_im_1593668895325","nickname":"123","bridge":[]}

更改的是  1234群组的用户列表

5）发送私聊消息
请求
{"uid":"web_im_1593668895325","type":100,"nickname":"123","msg":"hello 554","bridge":["web_im_1593668895325","web_im_1593668994253"],"groupId":""}
响应
{"type":2,"date":"2020-07-02 14:15:09","msg":"hello 554","uid":"web_im_1593668895325","nickname":"123","bridge":["web_im_1593668895325","web_im_1593668994253"],"groupId":"","status":1}

msg存储的 或者是具体的消息  或者是要广播的信息


