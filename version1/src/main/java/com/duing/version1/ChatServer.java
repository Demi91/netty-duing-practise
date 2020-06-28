package com.duing.version1;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

public class ChatServer {

    // 服务端通道
    private ServerSocketChannel channel;
    // 多路复用器
    private Selector selector;

    // 构造器  初始化
    public ChatServer() {
        try {
            // 打开一个服务端通道
            channel = ServerSocketChannel.open();
            // 打开多路复用器
            selector = Selector.open();
            // 声明端口号并绑定
            SocketAddress address = new InetSocketAddress(6666);
            channel.socket().bind(address);

            // 设置非阻塞的模式
            channel.configureBlocking(false);
            // 将通道注册进 选择器中  同时声明  关注这个通道的可接收事件
            channel.register(selector, SelectionKey.OP_ACCEPT);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 监听客户端的变化
    public void listen() {

        System.out.println("监听" + Thread.currentThread().getName());

        try {
            // 循环监听 （轮询）
            while (true) {

                // 询问选择器是否有事件要处理
                int num = selector.select();
                if (num == 0) {
                    continue;
                }

                // 确认有事件需要处理  找到选择器中  已选择的集合  selectedKeys
                // 遍历集合  拿到每一个SelectionKey  去判断当前处于四大事件中的哪种状态
                // 然后对应此状态进行逻辑处理
                Set<SelectionKey> set = selector.selectedKeys();
                Iterator<SelectionKey> iterator = set.iterator();
                while (iterator.hasNext()) {

                    SelectionKey key = iterator.next();
                    if (key.isAcceptable()) {
                        // 代表当前是可接收状态
                        // 接收  声明非阻塞  注册
                        SocketChannel clientChannel = channel.accept();
                        clientChannel.configureBlocking(false);
                        clientChannel.register(selector, SelectionKey.OP_READ);

                        // 代表有客户端和服务端进行连接
                        // 相当于群聊系统中的上线
                        System.out.println("用户" + clientChannel.socket().getRemoteSocketAddress()
                                + " 上线了");
                    }

                    if (key.isReadable()) {
                        // 代表当前是可读的状态
                        readData(key);
                    }

                    // 处理完此事件  需要移除  否则会重复处理
                    iterator.remove();
                }


            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    // alt enter可以直接根据参数类型 生成方法
    private void readData(SelectionKey key) {
        SocketChannel clientChannel = null;

        // 拿出channel
        try {

            clientChannel = (SocketChannel) key.channel();
            // 一定要通过buffer来处理数据
            ByteBuffer buffer = ByteBuffer.allocate(1024);

            int num = clientChannel.read(buffer);
            if (num > 0) {
                String msg = new String(buffer.array());
                System.out.println("msg :" + msg);

                // 再将数据广播给其他客户端（不包括此客户端本身）
                sendToOther(msg, clientChannel);
            }

        } catch (Exception e) {

            // 没有成功获取到通道  即为用户下线了
            System.out.println("用户" + clientChannel.socket().getRemoteSocketAddress()
                    + "下线了");

            // 取消注册关系
            key.cancel();
            // 关闭通道
            try {
                clientChannel.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }

    }


    private void sendToOther(String msg, SocketChannel selfChannel) throws IOException {

        // 找到所有的通道  然后排出channel自身
        Set<SelectionKey> set = selector.keys();
        for (SelectionKey key : set) {
            // 根据key  找出通道
            Channel otherChannel = key.channel();
            // 排出自身  找到所有其他的客户端连接
            if (otherChannel instanceof SocketChannel && otherChannel != selfChannel) {

                // 类型强转一下
                SocketChannel destChannel = (SocketChannel) otherChannel;
                // wrap(byte[] array) 直接将array数据存放到缓冲区中
                ByteBuffer buffer = ByteBuffer.wrap(msg.getBytes());
                destChannel.write(buffer);
            }


        }

    }


    public static void main(String[] args) {
        ChatServer server = new ChatServer();
        server.listen();
    }


}
