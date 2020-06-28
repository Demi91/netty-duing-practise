package com.duing.version2;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;
import java.util.Set;

public class MultiReactorServer {

    private Selector mainSelector;
    private Selector slaveSelector;

    private ServerSocketChannel serverSocketChannel;


    public MultiReactorServer() {

        try {
            // 主reactor处理连接事件
            mainSelector = Selector.open();
            // 从reactor处理读写事件
            slaveSelector = Selector.open();

            // 配置为非阻塞的
            serverSocketChannel.configureBlocking(false);

            SocketAddress address = new InetSocketAddress(9090);
            serverSocketChannel.socket().bind(address);

            // 将channel 注册进selector之中  进行监听   达到多路复用
            //   第一个注册的事件  往往是ACCEPT事件 （建立连接的事件）
            SelectionKey key = serverSocketChannel.register(mainSelector, SelectionKey.OP_ACCEPT);

            // 此时要传递 从selector  用于注册读事件
            Acceptor acceptor = new Acceptor(slaveSelector, serverSocketChannel);
            // 附加一个对象  用来处理事件时使用
            key.attach(acceptor);

            // 主从selector的监听逻辑需要分离
            new HandlerLoop(slaveSelector).run();


            // 不断循环遍历  是否有事件发生
            while (true) {
                // 返回当前发生的事件个数  num>0  要处理事件
                int num = mainSelector.select();
                if (num == 0) continue;

                // 接收事件集合然后遍历  SelectionKey代表一种事件
                Set<SelectionKey> set = mainSelector.selectedKeys();
                Iterator<SelectionKey> iterator = set.iterator();
                while (iterator.hasNext()) {
                    SelectionKey selectionKey = iterator.next();
                    // 避免重复处理
                    iterator.remove();

                    // 根据事件的类型  分发给Acceptor或者Handler进行处理
                    // 通过attachment方法  取出存储的对象
                    // 此时接收的一定是Accept事件  获取的是Acceptor对象
                    Runnable runnable = (Runnable) selectionKey.attachment();
                    runnable.run();
                }
            }


        } catch (IOException e) {
            e.printStackTrace();
        }


    }

}
