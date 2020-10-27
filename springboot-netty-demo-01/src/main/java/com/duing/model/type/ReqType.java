package com.duing.model.type;

// 1 创建连接  2 断开连接  10 创建群组  20 加入群组 100 发送消息

import lombok.Getter;

@Getter
public enum ReqType {
    CONN(1, "创建连接"),
    CANCEL(2, "断开连接"),
    ADD_GROUP(10, "创建群组"),
    JOIN_GROUP(20, "加入群组"),
    SEND_MSG(100, "发送消息(私聊、群聊)"),
    ;

    private int num;
    private String desc;

    ReqType(int num, String desc) {
        this.num = num;
        this.desc = desc;
    }


    public static ReqType getTypeByNum(int num) {
        ReqType[] reqTypes = ReqType.values();
        for (ReqType reqType : reqTypes) {
            if (num == reqType.getNum()) {
                return reqType;
            }
        }
        return ReqType.SEND_MSG;
    }

}
