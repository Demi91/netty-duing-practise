package com.duing.enums;

import lombok.Getter;

@Getter
public enum ReqType {

    CONN(1,"建立连接"),
    CANCEL(2,"断开连接"),
    ADD_GROUP(10,"创建群组"),
    JOIN_GROUP(20,"加入群组"),
    SEND_MSG(100,"发送消息"),
    ;

    private int num;
    private String desc;

    ReqType(int num, String desc) {
        this.num = num;
        this.desc = desc;
    }
}
