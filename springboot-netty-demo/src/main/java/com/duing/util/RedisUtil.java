package com.duing.util;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

// 实战中  经常是通过封装工具类来使用redis
@Component
public class RedisUtil {

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    public Object get(String key) {
        if (!redisTemplate.hasKey(key)) {
            return null;
        }
        Object obj = redisTemplate.opsForValue().get(key);
        return obj;
    }
}
