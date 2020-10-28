package com.duing.service;

import com.duing.data.LocalData;
import com.duing.model.GroupModel;
import com.duing.model.ReqModel;
import com.duing.model.RespModel;
import com.duing.model.UserModel;
import org.apache.tomcat.jni.Local;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


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

        // 从在线用户列表中  移除当前用户
        respModel.setMsg(reqModel.getNickname() + "退出聊天室");
        // 遍历列表  找到要移除的用户
        UserModel userModel = null;
        for (int i = 0; i < LocalData.userList.size(); i++) {
            UserModel tmpModel = LocalData.userList.get(i);
            if (tmpModel.getUid().equals(reqModel.getUid())) {
                userModel = tmpModel;
            }
        }
        LocalData.userList.remove(userModel);
        respModel.setUsers(LocalData.userList);
    }

    /**
     * {"type":1,"date":"2020-07-02 14:13:00","msg":"123创建了群newGroup","users":[{"nickname":"123","uid":"web_im_1593668895325","status":1},{"nickname":"554","uid":"web_im_1593668994253","status":1}],"groups":[{"id":1593668901314,"name":"222","users":[{"uid":"web_im_1593668895325","nickname":"123"},{"uid":"web_im_1593668994253","nickname":"554"}]},{"id":1593670380993,"name":"newGroup","users":[{"uid":"web_im_1593668895325","nickname":"123"}]}],"uid":"web_im_1593668895325","nickname":"123","bridge":[]}
     * <p>
     * 此时影响的是群组列表  参数是groups
     * 创建群组的用户  自动加入群组中
     *
     * @param reqModel
     * @param respModel
     */
    @Override
    public void addGroup(ReqModel reqModel, RespModel respModel) {
        respModel.setMsg(reqModel.getNickname() + "创建了群" + reqModel.getGroupName());

        // 把创建群的用户  加入群组的用户列表中
        UserModel self = new UserModel(reqModel.getNickname(), reqModel.getUid(), 1);
        List<UserModel> users = new ArrayList<>();
        users.add(self);

        // 用户的id是客户端生成  群的id是服务端生成的 （只是为了不更改前端代码）
        //  groupId的设计  应该具有唯一性  如果同一用户可能创建相同名字的群
        //       可以再增加一个时间戳
        String groupId = "group_" + reqModel.getUid() + "_" + reqModel.getGroupName();
        GroupModel groupModel = new GroupModel(reqModel.getGroupName(), groupId, users);

        LocalData.groupModelList.add(groupModel);
        respModel.setGroups(LocalData.groupModelList);
    }

    /**
     * {"type":1,"date":"2020-07-02 14:14:46","msg":"123加入了群1234","users":[{"nickname":"123","uid":"web_im_1593668895325","status":1},{"nickname":"554","uid":"web_im_1593668994253","status":1}],"groups":[{"id":1593668901314,"name":"222","users":[{"uid":"web_im_1593668895325","nickname":"123"},{"uid":"web_im_1593668994253","nickname":"554"}]},{"id":1593670380993,"name":"newGroup","users":[{"uid":"web_im_1593668895325","nickname":"123"}]},{"id":1593670445119,"name":"1234","users":[{"uid":"web_im_1593668994253","nickname":"554"},{"uid":"web_im_1593668895325","nickname":"123"}]}],"uid":"web_im_1593668895325","nickname":"123","bridge":[]}
     * <p>
     * 更改的是  1234群组的用户列表
     *
     * @param reqModel
     * @param respModel
     */
    @Override
    public void joinGroup(ReqModel reqModel, RespModel respModel) {

        // 先设置返回信息
        respModel.setMsg(reqModel.getNickname() + "加入了群" + reqModel.getGroupName());

        // 找到群  加入群成员
        for (GroupModel groupModel : LocalData.groupModelList) {
            if (groupModel.getId().equals(reqModel.getGroupId())) {
                UserModel userModel = new UserModel(
                        reqModel.getNickname(), reqModel.getUid(), 1);
                groupModel.getUsers().add(userModel);
            }
        }

        respModel.setGroups(LocalData.groupModelList);
    }

    /**
     * {"type":2,"date":"2020-07-02 14:13:06","msg":"123","uid":"web_im_1593668895325","nickname":"123","bridge":[],"groupId":1593670380993,"status":1}
     *
     * @param reqModel
     * @param respModel
     */
    @Override
    public void sendGroupMsg(ReqModel reqModel, RespModel respModel) {
        respModel.setMsg(reqModel.getMsg());

        respModel.setGroupId(reqModel.getGroupId());
        respModel.setStatus(1);
    }

    /**
     * 响应
     * {"type":2,"date":"2020-07-02 14:15:09","msg":"hello 554","uid":"web_im_1593668895325","nickname":"123","bridge":["web_im_1593668895325","web_im_1593668994253"],"groupId":"","status":1}
     *
     * @param reqModel
     * @param respModel
     */
    @Override
    public void sendPrivateMsg(ReqModel reqModel, RespModel respModel) {

        respModel.setMsg(reqModel.getMsg());

        respModel.setBridge(reqModel.getBridge());
        respModel.setGroupId("");
        respModel.setStatus(1);

    }
}
