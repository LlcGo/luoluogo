package com.lc.usercenter.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import javax.annotation.Resource;

/**
 * @Author Lc
 * @Date 2023/6/26
 * @Description
 */
@Configuration
public class RedisConfig {


    @Resource
    private RedisConnectionFactory factory;

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
//      连接工厂
        redisTemplate.setConnectionFactory(this.factory);
//        //        序列化配置
//        Jackson2JsonRedisSerializer<Object> objectJackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
////        value值的序列化采用FastJsonRedisSerializer
//        redisTemplate.setValueSerializer(objectJackson2JsonRedisSerializer);
////        hash值的序列化采用FastJsonRedisSerializer的方式
//        redisTemplate.setHashValueSerializer(objectJackson2JsonRedisSerializer);
//
////        key的序列化采用StringRedisSerializer
//        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
//        redisTemplate.setKeySerializer(stringRedisSerializer);
////        hash的key的序列化采用StringRedisSerializer的方式
//        redisTemplate.setHashKeySerializer(objectJackson2JsonRedisSerializer);
         redisTemplate.setKeySerializer(RedisSerializer.string());
        return redisTemplate;

    }
}