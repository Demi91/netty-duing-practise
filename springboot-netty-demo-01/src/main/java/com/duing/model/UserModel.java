package com.duing.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data @AllArgsConstructor
public class UserModel {

    private String nickname;
    private String uid;
    // 登录状态   在线1 离线2 等等
    private int status;
}
