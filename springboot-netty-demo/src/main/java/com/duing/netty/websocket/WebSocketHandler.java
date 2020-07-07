package com.duing.netty.websocket;

import com.duing.enums.ReqType;
import com.duing.enums.RespType;
import com.duing.mapper.LocalData;
import com.duing.model.GroupModel;
import com.duing.model.ReqModel;
import com.duing.model.RespModel;
import com.duing.model.UserModel;
import com.duing.service.ChatService;
import com.google.gson.Gson;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


// 泛型 代表的是处理数据的单位
// TextWebSocketFrame 是文本信息帧
@Component
@ChannelHandler.Sharable
public class WebSocketHandler extends
        SimpleChannelInboundHandler<TextWebSocketFrame> {

    @Autowired
    private ChatService chatService;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx,
                                TextWebSocketFrame msg) throws Exception {

        System.out.println("msg : " + msg.text());
        // 获取请求数据  解析json形式
        ReqModel model = new Gson().fromJson(msg.text(), ReqModel.class);

        RespModel respModel = new RespModel();
        // 先设置用户信息
        respModel.setUid(model.getUid());
        respModel.setNickname(model.getNickname());

        // 再设置服务器的当前时间  是最常用的日期格式
        LocalDateTime now = LocalDateTime.now();
        String date = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:SS"));
        respModel.setDate(date);

        // 给bridge设置初始值  bridge:[]
        List<String> defaultList = new ArrayList<>();
        respModel.setBridge(defaultList);

        // 先设置默认的处理类型
        respModel.setType(RespType.OPERA.getNum());

        // 判断请求类型
        ReqType type = ReqType.getTypeByNum(model.getType());
        switch (type) {
            case CONN:
                System.out.println(model.getNickname() + " 用户上线了");
                // 记录并返回 在线用户列表 以及 已创建的群组列表
                // 记录 用户和通道的关联关系
                LocalData.channelUserRel.put(model.getUid(), ctx.channel());
                chatService.addUser(model, respModel);
                break;
            case CANCEL:
                System.out.println(model.getNickname() + " 用户下线了");
                LocalData.channelUserRel.remove(
                        LocalData.channelUserRel.get(model.getUid()));
                chatService.delUser(model, respModel);
                break;
            case ADD_GROUP:
                System.out.println(model.getNickname() + " 创建了群组" + model.getGroupName());
                chatService.addGroup(model, respModel);
                break;
            case JOIN_GROUP:
                System.out.println(model.getNickname() + " 加入群组" + model.getGroupName());
                chatService.joinGroup(model, respModel);
                break;
            case SEND_MSG:
                // 具体识别为  消息类型时  再更改
                respModel.setType(RespType.MSG.getNum());
                // 判断是一对一  还是一对多
                if (model.getBridge().size() == 0) {
                    // 一对多
                    chatService.sendGroupMsg(model, respModel);
                } else {
                    chatService.sendPrivateMsg(model, respModel);
                }
                break;
        }


        System.out.println(new Gson().toJson(respModel));

        // 返回响应结果  还需要通知其他用户(通道)
        if (respModel.getType() == RespType.OPERA.getNum()) {
            List<Channel> channels = LocalData.getAllChannels();
            notifyChannels(channels, respModel);
            return;
        }

        // 根据一对一  或一对多的类型  找到需要接收通知的用户
        if (model.getBridge().size() > 0) {
            // 通知自身  和 要接收消息的用户
            String selfId = model.getBridge().get(0);
            Channel selfChannel = LocalData.channelUserRel.get(selfId);

            String otherId = model.getBridge().get(1);
            Channel otherChannel = LocalData.channelUserRel.get(otherId);

            List<Channel> channels = new ArrayList<Channel>() {
                {
                    add(selfChannel);
                    add(otherChannel);
                }
            };
            notifyChannels(channels, respModel);
            return;
        }

        // 一对多 群组消息
        List<Channel> channels = new ArrayList<>();
        // 通过群Id找到群对象 GroupModel  获取用户列表  根据列表中的UID  获取对应通道
        GroupModel groupModel = LocalData.getGroupById(model.getGroupId());
        for (UserModel userModel : groupModel.getUsers()) {
            Channel channel = LocalData.channelUserRel.get(userModel.getUid());
            channels.add(channel);
        }
        notifyChannels(channels, respModel);

    }

    // 广播给其他通道(在线用户)
    private void notifyChannels(List<Channel> channels, RespModel respModel) {
        for (Channel channel : channels) {
            TextWebSocketFrame resp = new TextWebSocketFrame(
                    new Gson().toJson(respModel));
            channel.writeAndFlush(resp);
        }

    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // 将channel添加到channel group中
        LocalData.channelList.add(ctx.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
//        super.channelInactive(ctx);
        LocalData.channelList.remove(ctx.channel());
    }
}
