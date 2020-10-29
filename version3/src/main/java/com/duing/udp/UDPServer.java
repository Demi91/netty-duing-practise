package com.duing.udp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Arrays;

public class UDPServer {

    public static void main(String[] args) throws Exception {
//       ServerSocket
        // 数据包
        DatagramSocket server = new DatagramSocket(8888);

        DatagramPacket packet = new DatagramPacket(new byte[255], 255);

        while (true) {

            server.receive(packet);

            byte[] receiveMsg = Arrays.copyOfRange(
                    packet.getData(), packet.getOffset(),
                    packet.getOffset() + packet.getLength()
            );

            System.out.println("UDP Server receive data:" + new String(receiveMsg));

//            packet.getAddress().getHostName();

            // 返回给客户端消息
            server.send(packet);
        }
    }
}
