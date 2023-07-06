package com.lc.usercenter.model.vo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author Lc
 * @Date 2023/6/30
 * @Description
 */
@Data
public class UserVo implements Serializable {

    private static final long serialVersionUID = 2837283726448287105L;
    /**
     * 用户名
     */
    private String userName;

    /**
     * 账户
     */
    private String userAccount;


    /**
     * 用户头像
     */
    private String avatarUrl;

    /**
     * 性别
     */
    private Integer gender;

    /**
     * 电话
     */
    private String phone;

    /**
     * 标签名
     */
    private String tags;

    /**
     * 邮箱
     */
    private String email;


    /**
     * 校验登录凭证
     */
    private String plantCode;
}
