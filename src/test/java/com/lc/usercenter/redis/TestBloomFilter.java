package com.lc.usercenter.redis;

import com.lc.usercenter.LuoLuoGoApplication;
import com.lc.usercenter.model.domain.UserTeam;
import com.lc.usercenter.service.UserTeamService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

import java.util.List;
import java.util.Random;

import static com.lc.usercenter.contact.UserContant.KEY_RBLOOM;

/**
 * @Author Lc
 * @Date 2023/7/7
 * @Description
 */
@SpringBootTest()
@RunWith(SpringRunner.class)
public class TestBloomFilter {

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private UserTeamService userTeamService;

    @Test
    public void test(){
        RBloomFilter<Object> bloomFilter = redissonClient.getBloomFilter(KEY_RBLOOM);
        List<UserTeam> userTeamList = userTeamService.list();
        userTeamList.forEach(userTeam -> {
           if(bloomFilter.contains(userTeam)){
               System.out.println(userTeam.getUserId() + "的队伍存在于bloomFilter");
           };
        });
    }

    @Test
    public void testTime(){
        Random random = new Random();
        for (int i = 0; i <= 100; i++){
            int sum = random.nextInt(3) + 1;
            System.out.println("随机数" + sum);
        }
    }
}
