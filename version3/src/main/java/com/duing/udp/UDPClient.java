package com.duing.udp;

import java.net.*;

public class UDPClient {

    public static void main(String[] args) throws Exception {

        byte[] msg = new String("hello udp server").getBytes();

        DatagramSocket client = new DatagramSocket();

        // 如果是广播  地址 更改为 255.255.255.255
//        InetAddress inetAddress2 = InetAddress.getByName("255.255.255.255");
        InetAddress inetAddress = InetAddress.getLocalHost();
        SocketAddress address = new InetSocketAddress(inetAddress, 8888);

        // 由 字节数组  数组长度  服务端地址（ip+port）
        DatagramPacket sendPacket = new DatagramPacket(msg, msg.length, address);
        client.send(sendPacket);
        client.close();

    }
}
