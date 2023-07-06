package com.lc.usercenter.once;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.read.listener.PageReadListener;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

/**
 * @Author Lc
 * @Date 2023/6/21
 * @Description
 */
@Slf4j
public class importExcel {
    public static void main(String[] args) {
        // 写法1：JDK8+ ,不用额外写一个DemoDataListener
        // since: 3.0.0-beta1
        String fileName = "C:\\Users\\Lc\\Desktop\\伙伴匹配\\luoluogo\\src\\main\\resources\\test.xls";
//        simpleRead(fileName);
        synchronousRead(fileName);
    }

    /**
     * 监听器读法 适合数据量大的情况
     * @param fileN
     */
    public static void simpleRead(String fileN) {
        // 写法1：JDK8+ ,不用额外写一个DemoDataListener
        // since: 3.0.0-beta1
        String fileName = fileN;
        // 这里默认每次会读取100条数据 然后返回过来 直接调用使用数据就行
        // 具体需要返回多少行可以在`PageReadListener`的构造函数设置
        EasyExcel.read(fileName, DemoData.class, new DemoDataListener()).sheet().doRead();
    }

    /**
     * 同步读 一次性全部读出来 不适合数据量大
     * @param fileN
     */
    public static void synchronousRead(String fileN) {
        String fileName = fileN;
        // 这里 也可以不指定class，返回一个list，然后读取第一个sheet 同步读取会自动finish
        List<Map<Integer, String>> listMap = EasyExcel.read(fileName).sheet().doReadSync();
        for (Map<Integer, String> data : listMap) {
            System.out.println(data);
        }
    }
}
