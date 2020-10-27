package com.duing.model;

import lombok.Data;

import java.util.List;

/**
 * {"uid":"web_im_1593668895325","type":10,"nickname":"123",
 * "groupName":"newGroup","bridge":[]}
 */
@Data
public class ReqModel {

    private String uid;
    // 1 创建连接  2 断开连接  10 创建群组  20 加入群组 100 发送消息
    private int type;

    private String nickname;
    private String groupId;
    private String groupName;

    // 一对一消息的用户id
    private List<String> bridge;
    private String msg;

}
