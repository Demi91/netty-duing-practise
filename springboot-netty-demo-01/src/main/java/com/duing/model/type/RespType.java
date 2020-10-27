package com.duing.model.type;

import lombok.Getter;

// 1 操作  2 消息
@Getter
public enum RespType {
    OPERA(1,"操作"),
    MSG(2,"消息")
    ;

    private int num;
    private String desc;

    RespType(int num, String desc) {
        this.num = num;
        this.desc = desc;
    }
}
