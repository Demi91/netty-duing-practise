package com.duing.serialize;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;

public class PersonInfo implements Serializable {

    private static final long serialVersionUID = 1L;


    private int id;
    private String name;

    public PersonInfo(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // 通过二进制字节流的方式 转化   和序列化的方式对比
    // 根据buffer缓冲区  获取字节数组
    public byte[] codeC() {
        ByteBuffer buffer = ByteBuffer.allocate(1024);

        byte[] value = this.name.getBytes();
        buffer.putInt(value.length);
        buffer.put(value);

        buffer.putInt(this.id);

        // 写完成 重置字节数组
        buffer.flip();
        value = null;

        byte[] result = new byte[buffer.remaining()];
        buffer.get(result);
        return result;

    }


    public static void main(String[] args) throws Exception {
        PersonInfo personInfo = new PersonInfo(1, "飘以成坠");

        // JDK原生序列化
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(personInfo);

        oos.flush();
        oos.close();

        byte[] result1 = bos.toByteArray();
        System.out.println("JDK原生序列化 流的长度" + result1.length);
        bos.close();

        System.out.println("通过buffer处理  流的长度" + personInfo.codeC().length);


    }
}
