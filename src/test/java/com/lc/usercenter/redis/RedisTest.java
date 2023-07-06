package com.lc.usercenter.redis;
import java.util.Date;

import com.lc.usercenter.model.domain.User;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * @Author Lc
 * @Date 2023/6/26
 * @Description
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class RedisTest {

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Test
    public void testRedis(){
        User user = new User();
        user.setId(0L);
        user.setUserName("");
        user.setUserAccount("");
        user.setUserPassword("");
        user.setAvatarUrl("");
        user.setGender(0);
        user.setPhone("");
        user.setTags("");
        user.setEmail("");
        user.setUserRole(0);
        user.setUserStatus(0);
        user.setUpdateTime(new Date());
        user.setCreateTime(new Date());
        user.setIsDelete(0);
        user.setPlantCode("");
        user.setProfile("");

        ValueOperations valueOperations = redisTemplate.opsForValue();
        valueOperations.set("user",user);
    }
}
