package com.duing.service;

import com.duing.mapper.LocalData;
import com.duing.model.ReqModel;
import com.duing.model.RespModel;
import com.duing.model.UserModel;
import org.springframework.stereotype.Service;

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

    }

    @Override
    public void joinGroup(ReqModel reqModel, RespModel respModel) {

    }

    @Override
    public void sendGroupMsg(ReqModel reqModel, RespModel respModel) {

    }

    @Override
    public void sendPrivateMsg(ReqModel reqModel, RespModel respModel) {

    }
}
