package com.lc.usercenter.common;

import com.lc.usercenter.model.domain.User;
import com.lc.usercenter.model.domain.UserTeam;
import com.lc.usercenter.service.UserService;
import com.lc.usercenter.service.UserTeamService;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;
import java.util.Random;

import static com.lc.usercenter.contact.UserContant.KEY_RBLOOM;

/**
 * @Author Lc
 * @Date 2023/7/7
 * @Description
 */
@Component
public class RBloomFilterDataInit {



    @Resource
    private RedissonClient redissonClient;

    @Resource
    private UserTeamService userTeamService;

//    @PostConstruct
    public void init(){
        RBloomFilter<Object> bloomFilter = redissonClient.getBloomFilter(KEY_RBLOOM);
        //预计100数据 误差0.01
        bloomFilter.tryInit(100L,0.01);
        List<UserTeam> userTeamList = userTeamService.list();
        userTeamList.forEach(bloomFilter::add);
    }



}
