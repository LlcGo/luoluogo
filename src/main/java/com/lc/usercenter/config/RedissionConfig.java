package com.lc.usercenter.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author Lc
 * @Date 2023/6/26
 * @Description
 */
@Configuration
@ConfigurationProperties(prefix = "spring.redis")
@Data
@Slf4j
public class RedissionConfig {

    private String host;

    private String port;

    private String password;
    @Bean
    public RedissonClient redissonClient(){
        // 1. Create config object
        Config config = new Config();
        String redisAddress = String.format("redis://%s:%s",host,port);
        log.info(redisAddress);
        config.useSingleServer().setAddress(redisAddress).setPassword(password).setDatabase(3);
        RedissonClient redisson = Redisson.create(config);
        return  redisson;
    }




}
