package com.duing.controller;


import com.alibaba.fastjson.JSON;
import com.duing.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RedisController {


    @Autowired
    private RedisUtil redisUtil;

    @RequestMapping("/getKey")
    public String getFromRedis(@RequestParam String key) {
        return JSON.toJSONString(redisUtil.get(key));
    }


    // 使用方式说明   对redis的使用  自定义了序列化方式
    // 先存后取  避免出错
    @RequestMapping("/setKey")
    public String setToRedis(@RequestParam String key, @RequestParam String value) {
        redisUtil.set(key, value);
        return "增加key: " + key;
    }

}
