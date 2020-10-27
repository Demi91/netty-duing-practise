package com.duing.service;

import com.duing.data.LocalData;
import com.duing.model.ReqModel;
import com.duing.model.RespModel;
import com.duing.model.UserModel;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ChatServiceImpl implements ChatService {

    /**
     * {"type":1,"date":"2020-07-02 14:12:23","msg":"123加入聊天室",
     * "users":[{"nickname":"123","uid":"web_im_1593668895325","status":1},
     * {"nickname":"554","uid":"web_im_1593668994253","status":1}],
     * "groups":[{"id":1593668901314,"name":"222","users":[{"uid":"web_im_1593668895325","nickname":"123"},{"uid":"web_im_1593668994253","nickname":"554"}]}],
     * "uid":"web_im_1593668895325","nickname":"123","bridge":[]}
     *
     * @param reqModel
     * @param respModel
     */
    @Override
    public void addUser(ReqModel reqModel, RespModel respModel) {
        respModel.setMsg(reqModel.getNickname() + "加入聊天室");
        UserModel userModel = new UserModel(reqModel.getNickname(), reqModel.getUid(), 1);
        LocalData.userList.add(userModel);

        respModel.setUsers(LocalData.userList);
        respModel.setGroups(LocalData.groupModelList);

    }

    @Override
    public void delUser(ReqModel reqModel, RespModel respModel) {

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
