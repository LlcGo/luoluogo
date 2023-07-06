package com.lc.usercenter;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.lc.usercenter.model.domain.User;
import com.lc.usercenter.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StopWatch;

import javax.annotation.Resource;

/**
 * @Author Lc
 * @Date 2023/6/25
 * @Description
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class insertUsers {
    @Resource
    private UserService userService;

    /**
     * 多线程并发执行
     */
    @Test
    public void testThread(){
        //一开始60个线程，最大1000，有10000秒没有执行这个线程池就销毁，
        // 如果这个队列到10000满了就开始加怎加线程到1000 如果到了1000还有不能处理完多的直接扔掉
        ThreadPoolExecutor threadPoolExecutor =
                new ThreadPoolExecutor(60, 1000, 10000, TimeUnit.SECONDS, new ArrayBlockingQueue<>(10000));
        StopWatch stopWatch = new StopWatch();
        final int  NUM = 100000;

        stopWatch.start();
        int j = 0;
        List<CompletableFuture<Void>> futureList = new ArrayList<>();
        for (int i = 0; i < 25 ; i++) {
            //转成并发安全的集合
            List<User> users = Collections.synchronizedList(new ArrayList<>());
            while (true){
                j++;
                User user = new User();
                user.setUserName("Ji"+i);
                user.setUserAccount("Ji"+i);
                user.setUserPassword("123456");
                user.setAvatarUrl("http://localhost:8080/api/ikun.jpg");
                user.setGender(0);
                user.setPhone("12316546");
                user.setTags("[]");
                user.setEmail("164"+NUM +"@qq.com");
                user.setUpdateTime(new Date());
                user.setCreateTime(new Date());
                user.setPlantCode("66"+i);
                user.setProfile("666"+i);
                users.add(user);
                if(j % 10000 == 0){
                    break;
                }
            }
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                System.out.println("ThreadWork =" + Thread.currentThread().getName());
                userService.saveBatch(users, 4000);
            },threadPoolExecutor);
            //加入一个任务
            futureList.add(future);
        }
        //转换成数据执行
        CompletableFuture.allOf(futureList.toArray(new CompletableFuture[]{})).join();
        stopWatch.stop();
        System.out.println(stopWatch.getTotalTimeMillis());
    }



    @Test
    public void insert(){
        StopWatch stopWatch = new StopWatch();
        final int  NUM = 100000;
        ArrayList<User> users = new ArrayList<>();
        stopWatch.start();
        for (int i = 1; i < NUM ; i++) {
            User user = new User();
            user.setUserName("Ji"+i);
            user.setUserAccount("Ji"+i);
            user.setUserPassword("123456");
            user.setAvatarUrl("http://localhost:8080/api/ikun.jpg");
            user.setGender(0);
            user.setPhone("12316546");
            user.setTags("[]");
            user.setEmail("164"+NUM +"@qq.com");
            user.setUpdateTime(new Date());
            user.setCreateTime(new Date());
            user.setPlantCode("66"+i);
            user.setProfile("666"+i);
            users.add(user);
        }
        stopWatch.stop();
        userService.saveBatch(users,1000);
        System.out.println(stopWatch.getTotalTimeMillis());
    }
}
