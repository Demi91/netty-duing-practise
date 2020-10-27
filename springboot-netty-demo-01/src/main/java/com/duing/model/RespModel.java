package com.duing.model;

import lombok.Data;

import java.util.List;

/**
 * {"type":1,"date":"2020-07-02 14:12:23","msg":"123加入聊天室",
 * "users":[{"nickname":"123","uid":"web_im_1593668895325","status":1},
 * {"nickname":"554","uid":"web_im_1593668994253","status":1}],
 * "groups":[{"id":1593668901314,"name":"222","users":[{"uid":"web_im_1593668895325","nickname":"123"},{"uid":"web_im_1593668994253","nickname":"554"}]}],
 * "uid":"web_im_1593668895325","nickname":"123","bridge":[]}
 */
@Data
public class RespModel {

    private String uid;
    private String nickname;
    // 1 操作  2 消息
    private int type;
    private String date;
    // 如果是操作类型  msg记录的是广播消息
    // 如果是消息类型  msg是具体的消息数据
    private String msg;

    // 在线用户列表
    private List<UserModel> users;
    // 群组列表
    private List<GroupModel> groups;

    private List<String> bridge;

    private String groupId;
    private int status;

}
