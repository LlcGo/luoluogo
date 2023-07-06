package com.lc.usercenter.once;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class DemoData {
    /**
     * id
     */
    @ExcelProperty("成员编号")
    private String planeCode;

    /**
     * 用户昵称
     */
    @ExcelProperty("成员昵称")
    private String username;
}