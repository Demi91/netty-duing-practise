package com.duing.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class RedisUtil {

//    @Autowired
//    private RedisTemplate<String, Object> redisTemplate;
//
//
//    // Redis 各种类型所对应的操作方法
//    //  opsForValue 对应 String
//    //  opsForList 对应 List
//    //  opsForSet 对应 Set  集合
//    //  opsForZSet  对应 ZSet  有序集合
//    //  opsForHash  对应 Hash
//    public void set(String key, Object value) {
//        redisTemplate.opsForValue().set(key, value);
//
////        redisTemplate.opsForList()
////        redisTemplate.opsForSet()
////        redisTemplate.opsForZSet()
////        redisTemplate.opsForHash()
//    }
//
//
//    // 递增
//    public long incr(String key,long delta){
//        // 智能识别要编写代码的工具 aiXcoder
//        return redisTemplate.opsForValue().increment(key,delta);
//    }
//
//    // 递减
//    public long decr(String key,long delta){
//        return redisTemplate.opsForValue().increment(key,-delta);
//    }
//
//    // 将redis的命令  很方便的转化为工具类中的代码
//    // Hash   hget  hset  hmget  hmset
//    //    课后作业  尝试实现以下
//
//    // Set  sSet  sGet  Remove ...
//    // List lGet  lSet
//
//    public void set(String key, Object value, long timeout) {
//        redisTemplate.opsForValue().set(key, value, timeout, TimeUnit.SECONDS);
//    }
//
//    public long getExpire(String key) {
//        if (redisTemplate.hasKey(key)) {
//            return redisTemplate.getExpire(key);
//        }
//        // 如果key存在  但没有失效时间  会返回-1
//        // 如果key不存在  返回-2
//        return -2l;
//    }
//
//    public Object get(String key) {
//        if (redisTemplate.hasKey(key)) {
//            return redisTemplate.opsForValue().get(key);
//        }
//        return null;
//    }
//
//    public boolean delete(String key) {
//        redisTemplate.delete(key);
//        if (redisTemplate.hasKey(key)) {
//            return false;
//        }
//        return true;
//    }


}
