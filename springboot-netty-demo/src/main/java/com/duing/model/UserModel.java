package com.duing.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data @AllArgsConstructor
public class UserModel {

    private String uid;
    private String nickname;
    // 状态  1 在线  0 离线
    private int status;

    public UserModel(String uid, String nickname) {
        this.uid = uid;
        this.nickname = nickname;
    }
}
