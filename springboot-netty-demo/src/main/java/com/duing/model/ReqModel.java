package com.duing.model;

import lombok.Data;

import java.util.List;

// 请求所需要的字段
@Data
public class ReqModel {

    // 对应ReqType
    private int type;
    // 用户相关
    private String uid;
    private String nickname;
    // 群组相关
    private String groupId;
    private String groupName;
    // 消息相关
    private String msg;
    private List<String> bridge;
}
