package com.lc.usercenter;
import java.util.Date;

import com.lc.usercenter.mapper.UserMapper;
import com.lc.usercenter.model.domain.User;
import com.lc.usercenter.service.UserService;
import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import org.apache.tomcat.util.security.MD5Encoder;
import org.junit.Assert;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author Lc
 * @Date 2023/4/8
 * @Description
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestSQL {

    @Resource
    private UserMapper userMapper;

    @Resource
    private UserService userService;

    @Test
    public void test01(){
        List<User> userList = userMapper.selectList(null);
        Assert.assertEquals(5,userList.size());
        userList.forEach(System.out::println);
    }

    @Test
    public void TestInsert(){
        User user = new User();
        user.setPlantCode("1");
        user.setUserName("test");
        user.setUserAccount("admin");
        user.setUserPassword("12345678");
        user.setGender(0);
        user.setPhone("123");
        user.setEmail("123");
        user.setUserStatus(0);
        user.setUpdateTime(new Date());
        user.setCreateTime(new Date());
        user.setIsDelete(0);
        boolean save = userService.save(user);
        Assert.assertTrue(save);
    }

    @Test
    public void testPassword(){
        String password = DigestUtils.md5DigestAsHex(("data" + "password").getBytes());
        System.out.println(password);
    }

    @Test
    public void testZZ(){
        String userAccount = "1231???????";
        String regEx = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(regEx).matcher(userAccount);
        System.out.println(matcher.find());

    }
}
