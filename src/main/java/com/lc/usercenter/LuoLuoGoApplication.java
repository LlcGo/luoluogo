package com.lc.usercenter;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.lc.usercenter.mapper")
public class LuoLuoGoApplication {

    public static void main(String[] args) {
        SpringApplication.run(LuoLuoGoApplication.class, args);
    }

}
