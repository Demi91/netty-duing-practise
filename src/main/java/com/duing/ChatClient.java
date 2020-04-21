package com.duing;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;

public class ChatClient {

    // 确认ip地址加端口号

    // 客户端的通道
    private SocketChannel channel;
    // 管理通道和事件之间的注册关系
    private Selector selector;


    // 初始化
    public ChatClient() throws Exception {

        selector = Selector.open();

        SocketAddress address = new InetSocketAddress("127.0.0.1", 6666);
        channel = SocketChannel.open(address);

        channel.configureBlocking(false);
        // 将通道注册到选择器
        channel.register(selector, SelectionKey.OP_READ);

        System.out.println("用户" + channel.getLocalAddress() + "上线了");

    }

    // 向服务端发送数据
    public void sendData(String msg) {
        try {
            ByteBuffer buffer = ByteBuffer.wrap(msg.getBytes());
            channel.write(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 读取服务端推送的数据
    public void readData() {
        // 监听当前通道有没有读事件的发生

        try {
            int num = selector.select();
            if (num > 0) {

                Set<SelectionKey> set = selector.selectedKeys();
                Iterator<SelectionKey> iterator = set.iterator();
                while (iterator.hasNext()) {

                    SelectionKey key = iterator.next();
                    if (key.isReadable()) {
                        // 得到对应的通道
                        SocketChannel socketChannel = (SocketChannel) key.channel();
                        // 声明要处理的buffer
                        ByteBuffer buffer = ByteBuffer.allocate(1024);
                        socketChannel.read(buffer);

                        // 转成字符串输出
                        String msg = new String(buffer.array());
                        System.out.println(msg);
                    }

                    // 注意 不要忘
                    iterator.remove();
                }
            }


        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public static void main(String[] args) throws Exception {
        // 创建一个ChatClient对象
        final ChatClient client = new ChatClient();

        // 循环从服务端读取数据  设置时间间隔  比如每2s
        new Thread() {

            public void run() {

                while (true) {
                    client.readData();

                    try {
                        Thread.currentThread().sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }

        }.start();

        // 客户端能够接受键盘输入  可以随时发消息给服务端
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String str = scanner.nextLine();
            client.sendData(str);
        }

    }

}
