package com.duing.version1.serialize;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;

public class PersonInfo2 implements Serializable {

    private static final long serialVersionUID = 1L;


    private int id;
    private String name;

    public PersonInfo2(int id, String name) {
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
    public byte[] codeC(ByteBuffer buffer) {
        // 重置索引
        buffer.clear();

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
        PersonInfo2 personInfo = new PersonInfo2(2, "小海豚");

        int loop = 1000000;
        // JDK原生序列化
        ByteArrayOutputStream bos = null;
        ObjectOutputStream oos = null;

        long startTime = System.currentTimeMillis();

        for (int i = 0; i < loop; i++) {
            bos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(bos);
            oos.writeObject(personInfo);

            oos.flush();
            oos.close();
            bos.close();

        }

        long endTime = System.currentTimeMillis();

        System.out.println("JDK原生序列化 耗费时间" + (endTime - startTime) + "ms");

        ByteBuffer buffer = ByteBuffer.allocate(1024);
        startTime = System.currentTimeMillis();
        for (int i = 0; i < loop; i++) {
            personInfo.codeC(buffer);
        }
        endTime = System.currentTimeMillis();

        System.out.println("通过buffer处理  耗费时间" + (endTime - startTime) + "ms");


    }
}
