package com.lc.usercenter.model.domain;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName user
 */
@TableName(value ="user")
@Data
public class User implements Serializable {
    /**
     * 用户id
     */
    @TableId
    private long id;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 账户
     */
    private String userAccount;

    /**
     * 密码
     */
    private String userPassword;

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
     * 用户权限 0 普通用户 1 管理员
     */
    private Integer userRole;
    /**
     * 0 正常 
     */
    private Integer userStatus;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 0正常 1删除 (逻辑删除)
     */
    @TableLogic
    private Integer isDelete;

    /**
     * 校验登录凭证
     */
    private String plantCode;

    /**
     * 个人简介
     */
    private String profile;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}