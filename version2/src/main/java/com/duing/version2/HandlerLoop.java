package com.duing.version2;

import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.Set;

// 用于处理 从Selector的监听轮询
public class HandlerLoop implements Runnable {


    private Selector selector;

    public HandlerLoop(Selector selector) {
        this.selector = selector;
    }

    @Override
    public void run() {


        try {
            // 不断循环遍历  是否有事件发生
            while (true) {
                // 返回当前发生的事件个数  num>0  要处理事件
                int num = selector.select();
                if (num == 0) continue;

                // 接收事件集合然后遍历  SelectionKey代表一种事件
                Set<SelectionKey> set = selector.selectedKeys();
                Iterator<SelectionKey> iterator = set.iterator();
                while (iterator.hasNext()) {
                    SelectionKey selectionKey = iterator.next();
                    // 避免重复处理
                    iterator.remove();

                    // 根据事件的类型  分发给Acceptor或者Handler进行处理
                    // 通过attachment方法  取出存储的对
                    // 此时接收的一定是读写事件  获取的是Handler对象
                    Runnable runnable = (Runnable) selectionKey.attachment();
                    runnable.run();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
