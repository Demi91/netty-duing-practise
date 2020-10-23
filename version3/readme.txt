

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


