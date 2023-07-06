package com.lc.usercenter.job;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lc.usercenter.model.domain.User;

import com.lc.usercenter.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Author Lc
 * @Date 2023/6/26
 * @Description
 */

@Component
@Slf4j
public class PreCache {

    @Resource
    private UserService userService;

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private RedissonClient redissonClient;

    //建议别写死在代码里
    private List<Long> userList = Arrays.asList(1L);

    @Scheduled(cron = "0 2 2 * * * ")
    public void caCheRedis(){
        RLock lock = redissonClient.getLock("llg:precachejob:docache:lock");
            //time,多少秒后重试 leaseTime 锁过期时间 -1 才能触发看门狗机制
        try {
            if(lock.tryLock(0,-1,TimeUnit.MILLISECONDS)){
                    for (Long userId : userList){
                        String redisKey =  String.format("LLG:RECOMMEND:%s",userId);
                        ValueOperations valueOperations = redisTemplate.opsForValue();
                        QueryWrapper<User> wrapper = new QueryWrapper<>();
                        Page<User> userPage = userService.page(new Page<>(1, 20), wrapper);
                        valueOperations.set(redisKey,userPage,10000, TimeUnit.SECONDS);
                    }
            }
        } catch (InterruptedException e) {
            log.info("PrecaCheRedis error",e);
        } finally {
            //判断是否当前是自己的锁
            //释放掉自己的锁
            //一定要放在这里无论如何都要执行
            if(lock.isHeldByCurrentThread()){
                lock.unlock();
            }
        }


    }
}
