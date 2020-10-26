package com.duing.config;

import com.alibaba.fastjson.support.spring.GenericFastJsonRedisSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

//@Configuration
//@PropertySource("classpath:redis.properties")
public class RedisConfig {


//    @Bean
//    public RedisTemplate<String,Object> redisTemplate(
//            RedisConnectionFactory redisConnectionFactory){
//
//        RedisTemplate<String,Object> redisTemplate = new RedisTemplate<>();
//        redisTemplate.setConnectionFactory(redisConnectionFactory);
//
//        // 需要对key和value 进行序列化    定义序列化的方式
//        StringRedisSerializer keySerializer = new StringRedisSerializer();
//        GenericFastJsonRedisSerializer valueSerializer
//                = new GenericFastJsonRedisSerializer();
//
//        redisTemplate.setKeySerializer(keySerializer);
//        redisTemplate.setValueSerializer(valueSerializer);
//
//        redisTemplate.setHashKeySerializer(keySerializer);
//        redisTemplate.setHashValueSerializer(valueSerializer);
//
//        return redisTemplate;
//    }
}
