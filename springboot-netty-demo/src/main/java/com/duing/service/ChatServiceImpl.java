package com.duing.service;

import com.duing.mapper.LocalData;
import com.duing.model.GroupModel;
import com.duing.model.ReqModel;
import com.duing.model.RespModel;
import com.duing.model.UserModel;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ChatServiceImpl implements ChatService {

    @Override
    public void addUser(ReqModel reqModel, RespModel respModel) {

        respModel.setMsg(reqModel.getNickname() + "加入聊天室");
        UserModel userModel = new UserModel(
                reqModel.getUid(), reqModel.getNickname(), 1);
        LocalData.userList.add(userModel);

        respModel.setUsers(LocalData.userList);
        respModel.setGroups(LocalData.groupModelList);

    }

    @Override
    public void delUser(ReqModel reqModel, RespModel respModel) {

        respModel.setMsg(reqModel.getNickname() + "退出聊天室");
        UserModel userModel = null;
        for (int i = 0; i < LocalData.userList.size(); i++) {
            UserModel tmpModel = LocalData.userList.get(i);
            if (tmpModel.getUid().equals(reqModel.getUid())) {
                userModel = tmpModel;
                break;
            }
        }
        LocalData.userList.remove(userModel);
        respModel.setUsers(LocalData.userList);

    }

    @Override
    public void addGroup(ReqModel reqModel, RespModel respModel) {
        respModel.setMsg(reqModel.getNickname() + "创建了群 " + reqModel.getGroupName());
        // 把创建者加入到群组的成员列表中
        UserModel self = new UserModel(reqModel.getUid(), reqModel.getNickname());
        List<UserModel> users = new ArrayList<>();
        users.add(self);

        // 此处  开源项目的设计是  UID由客户端创建  Group_ID由服务端创建
        // 但是ID几乎都是服务端创建的   客户端创建有极大风险
        // ID的设计方式很多样  此处只是做了简单拼接  如果群组可能重名  可以再增加一个时间戳
        String groupId = "group_" + reqModel.getUid() + "_" + reqModel.getGroupName();
        GroupModel groupModel = new GroupModel(groupId, reqModel.getGroupName(), users);

        LocalData.groupModelList.add(groupModel);
        respModel.setGroups(LocalData.groupModelList);

    }

    @Override
    public void joinGroup(ReqModel reqModel, RespModel respModel) {

        respModel.setMsg(reqModel.getNickname() + "加入了群 " + reqModel.getGroupName());

        for (GroupModel groupModel : LocalData.groupModelList) {
            if (groupModel.getId().equals(reqModel.getGroupId())) {
                UserModel self = new UserModel(reqModel.getUid(), reqModel.getNickname());
                groupModel.getUsers().add(self);
                break;
            }
        }
        respModel.setGroups(LocalData.groupModelList);
    }

    @Override
    public void sendGroupMsg(ReqModel reqModel, RespModel respModel) {
        respModel.setMsg(reqModel.getMsg());

        respModel.setGroupId(reqModel.getGroupId());
        respModel.setStatus(1);
    }

    @Override
    public void sendPrivateMsg(ReqModel reqModel, RespModel respModel) {
        respModel.setMsg(reqModel.getMsg());
        respModel.setBridge(reqModel.getBridge());
        respModel.setGroupId("");
        respModel.setStatus(1);
    }
}
