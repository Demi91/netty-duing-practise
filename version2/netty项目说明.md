# Netty项目

整合springboot的流程

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