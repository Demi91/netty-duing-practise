package com.duing.enums;

import lombok.Getter;

@Getter
public enum RespType {

    OPERA(1,"操作类处理"),
    MSG(2,"消息类处理")
    ;

    private int num;
    private String desc;

    RespType(int num, String desc) {
        this.num = num;
        this.desc = desc;
    }
}
