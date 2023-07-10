package com.lc.usercenter.service;

import com.lc.usercenter.utils.Aig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Author Lc
 * @Date 2023/7/7
 * @Description
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestAigUtil {

    @Test
    public void test(){
        List<String> strings1 = Arrays.asList("java","男","大一","乒乓");
        List<String> strings2 = Arrays.asList("java","男","大一","乒乓");
        List<String> strings3 = Arrays.asList("java","大三","女");
        int i1 = Aig.minDistance(strings1, strings2);
        int i2 = Aig.minDistance(strings1, strings3);
        System.out.println(strings1.equals(strings3));
        System.out.println(i2);
    }
}
