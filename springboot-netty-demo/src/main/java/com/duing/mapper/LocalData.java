package com.duing.mapper;

import com.duing.model.GroupModel;
import com.duing.model.UserModel;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.util.*;

public class LocalData {

    // 通道列表
    public final static ChannelGroup channelList =
            new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);


    // 存储通道和用户ID的映射关系  用来获取消息通知的通道
    public final static Map<String, Channel> channelUserRel = new HashMap<>();

    // 在线用户列表
    public static final List<UserModel> userList = new ArrayList<>();
    // 群组列表
    public static final List<GroupModel> groupModelList = new ArrayList<>();


    public static List<Channel> getAllChannels() {
        List<Channel> channels = new ArrayList<>();
        Iterator<Channel> iterator = channelList.iterator();
        while (iterator.hasNext()) {
            Channel channel = iterator.next();
            channels.add(channel);
        }
        return channels;
    }


    public static GroupModel getGroupById(String id) {
        for (GroupModel groupModel : groupModelList) {
            if (groupModel.getId().equals(id)) {
                return groupModel;
            }
        }
        return null;
    }

}
