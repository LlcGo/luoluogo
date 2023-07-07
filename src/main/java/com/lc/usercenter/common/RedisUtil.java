package com.lc.usercenter.common;

import java.util.Random;

/**
 * @Author Lc
 * @Date 2023/7/7
 * @Description
 */
public class RedisUtil {
    /**
     * 1-3随机数
     * @return
     */
    public static Long expTime(){
        Random random = new Random();
            int sum = random.nextInt(3) + 1;
            return (long) sum;
        }
}

