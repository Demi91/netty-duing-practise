package com.duing.version2.udp;

import java.net.*;

// 单播的通信方式
public class UDPClient {

    public static void main(String[] args) throws Exception {

        // 如果使用多播 使用MulticastSocket子类

        DatagramSocket socket = new DatagramSocket();

        InetAddress inetAddress = InetAddress.getLocalHost();
        // 如果使用广播  将地址设置为255.255.255.255
//        InetAddress inetAddress = InetAddress.getByName("255.255.255.255");

        SocketAddress socketAddress = new InetSocketAddress(inetAddress, 6789);

        byte[] msg = new String("hello udp server").getBytes();
        DatagramPacket packet = new DatagramPacket(msg, msg.length, socketAddress);
        socket.send(packet);

        socket.close();
    }
}
