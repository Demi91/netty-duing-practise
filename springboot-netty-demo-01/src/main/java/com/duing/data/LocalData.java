package com.duing.data;

import com.duing.model.GroupModel;
import com.duing.model.UserModel;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.util.*;

// 数据存储逻辑
public class LocalData {

    // 通道列表
    public final static ChannelGroup channelList =
            new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    // 需要拿到用户id后找到其对应的通道
    // 记录 uid 和 channel的关联关系
    public final static Map<String, Channel> channelUserRel = new HashMap<>();

    // 在线用户的列表
    public final static List<UserModel> userList = new ArrayList<>();

    // 群组列表
    public final static List<GroupModel> groupModelList = new ArrayList<>();


    public static List<Channel> getAllChannel() {
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
