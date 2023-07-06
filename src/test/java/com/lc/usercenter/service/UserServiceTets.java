package com.lc.usercenter.service;

import com.lc.usercenter.model.domain.User;
import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @Author Lc
 * @Date 2023/4/10
 * @Description
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class UserServiceTets {

    @Resource
    private UserService userService;

    @Test
    public void register() {
        String userPassword = "123456789";
        String userAccount = "123";
        String confPassword = "123456789";
        String plantCode = "1";
        long register = userService.register(userAccount, userPassword, confPassword,plantCode);
        Assertions.assertEquals(-1,register);
        userAccount = "????????? asdasdl";
        register = userService.register(userAccount, userPassword, confPassword,plantCode);
        Assertions.assertEquals(-1,register);
        userAccount = "lc666";
        userPassword = "12345678";
        confPassword = "1234567";
        register = userService.register(userAccount, userPassword, confPassword,plantCode);
        Assertions.assertEquals(-1,register);
        userAccount = "lc666";
        register = userService.register(userAccount, userPassword, confPassword,plantCode);
        Assertions.assertEquals(-1,register);
    }


    @Test
    public void searchUserByTageName() {
        List<String> listTageName = Arrays.asList("java","python");
        List<User> users = userService.searchUserByTageName(listTageName);
        Assert.assertNotNull(users);
    }


}
