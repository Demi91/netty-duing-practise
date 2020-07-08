package com.duing.version2.udp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Arrays;

public class UDPServer {

    public static void main(String[] args) throws Exception {

        // 通信的一端
        DatagramSocket socket = new DatagramSocket(6789);
        // 数据格式的报文
        DatagramPacket packet = new DatagramPacket(new byte[1024], 1024);

        while (true) {
            socket.receive(packet);
            // 拷贝 字节数组 从起始位置 到终点位置
            byte[] msg = Arrays.copyOfRange(packet.getData(),
                    packet.getOffset(), packet.getOffset() + packet.getLength());

            System.out.println("Receive Data:" + new String(msg));
            System.out.println("Receive Data From " + packet.getAddress().getHostAddress());

            socket.send(packet);
        }
    }
}
