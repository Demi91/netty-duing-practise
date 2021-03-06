package com.duing.service;

import com.duing.model.ReqModel;
import com.duing.model.RespModel;

// 包含所有被调用的业务逻辑接口
public interface ChatService {

    void addUser(ReqModel reqModel, RespModel respModel);

    void delUser(ReqModel reqModel, RespModel respModel);

    void addGroup(ReqModel reqModel, RespModel respModel);

    void joinGroup(ReqModel reqModel, RespModel respModel);

    void sendGroupMsg(ReqModel reqModel, RespModel respModel);

    void sendPrivateMsg(ReqModel reqModel, RespModel respModel);

}
