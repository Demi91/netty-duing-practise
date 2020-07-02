package com.duing.model;

import lombok.Data;

@Data
public class UserModel {

    private String uid;
    private String nickname;
    // 状态  1 在线  0 离线
    private int status;

}
