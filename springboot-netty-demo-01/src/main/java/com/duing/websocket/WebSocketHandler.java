package com.duing.websocket;

import com.duing.data.LocalData;
import com.duing.model.GroupModel;
import com.duing.model.ReqModel;
import com.duing.model.RespModel;
import com.duing.model.UserModel;
import com.duing.model.type.ReqType;
import com.duing.model.type.RespType;
import com.duing.service.ChatService;
import com.google.gson.Gson;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
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
public class WebSocketHandler
        extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    @Autowired
    private ChatService chatService;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg)
            throws Exception {

//        System.out.println("msg : " + msg.text());
//        Channel channel = ctx.channel();
//        TextWebSocketFrame resp = new TextWebSocketFrame("hello client from websocket server");
//        channel.writeAndFlush(resp);

        System.out.println("req:" + msg.text());

        // 数据格式为json  转化到ReqModel
        ReqModel model = new Gson().fromJson(msg.text(), ReqModel.class);
        // 创建需要返回的结果
        RespModel respModel = new RespModel();

        // 先设置用户信息
        respModel.setUid(model.getUid());
        respModel.setNickname(model.getNickname());

        // 再设置系统时间
        LocalDateTime now = LocalDateTime.now();
        String date = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:SS"));
        respModel.setDate(date);

        // 给bridge设置初始值   bridge:[]
        // bridge不能为null  客户端未兼容此情况(会报错)
        List<String> defaultList = new ArrayList<>();
        respModel.setBridge(defaultList);

        ReqType type = ReqType.getTypeByNum(model.getType());
        // 给响应类型 设置一个默认值
        respModel.setType(RespType.OPERA.getNum());
        switch (type) {
            case CONN:
                System.out.println("用户上线了");
                // 增加用户id和通道之间的关联关系
                LocalData.channelUserRel.put(model.getUid(), ctx.channel());
                chatService.addUser(model, respModel);
                break;
            case CANCEL:
                System.out.println("用户下线了");
                LocalData.channelUserRel.remove(
                        LocalData.channelUserRel.get(model.getUid())
                );
                chatService.delUser(model, respModel);
                break;
            case ADD_GROUP:
                chatService.addGroup(model, respModel);
                break;
            case JOIN_GROUP:
                chatService.joinGroup(model, respModel);
                break;
            case SEND_MSG:
                respModel.setType(RespType.MSG.getNum());
                // 进一步判断  是群聊还是私聊
                if (model.getBridge().size() == 0) {
                    chatService.sendGroupMsg(model, respModel);
                } else {
                    chatService.sendPrivateMsg(model, respModel);
                }
                break;
        }


        System.out.println("resp:" + new Gson().toJson(respModel));

        if (respModel.getType() == RespType.OPERA.getNum()) {
            // 广播给所有通道 - 所有在线用户
            List<Channel> channels = LocalData.getAllChannel();
            notifyChannel(channels, respModel);
            return;
        }


        // 一定是消息  先处理私聊
        if (model.getBridge().size() > 0) {

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

            notifyChannel(channels, respModel);
            return;
        }

        // 群聊的逻辑

        // 找到群对应的所有用户   找到用户对应的所有通道
        List<Channel> channels = new ArrayList<>();
        GroupModel groupModel = LocalData.getGroupById(model.getGroupId());
        for (UserModel user : groupModel.getUsers()) {
            Channel channel = LocalData.channelUserRel.get(user.getUid());
            channels.add(channel);
        }
        notifyChannel(channels, respModel);

    }

    public void notifyChannel(List<Channel> channels, RespModel respModel) {
        for (Channel channel : channels) {
            // 封装过程  respModel对象 -> json数据 -> websocket使用的文本帧
            TextWebSocketFrame resp = new TextWebSocketFrame(
                    new Gson().toJson(respModel));
            channel.writeAndFlush(resp);
        }
    }


    // 用户刚刚创建连接时  会调用
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
//        super.channelActive(ctx);
        LocalData.channelList.add(ctx.channel());

    }

    // 用户断开连接时  会调用
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
//        super.channelInactive(ctx);
        LocalData.channelList.remove(ctx.channel());
    }
}
