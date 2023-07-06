package com.lc.usercenter.model.domain;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.Date;
import lombok.Data;

/**
 * 队伍表
 * @TableName team
 */
@TableName(value ="team")
@Data
public class Team implements Serializable {
    /**
     * 队伍id 
     */
    @TableId(type = IdType.AUTO)
    private Long teamId;

    /**
     * 队伍主人的id
     */
    private Long userId;

    /**
     * 队伍描述
     */
    private String description;

    /**
     * 队伍名称
     */
    private String teamName;


    /**
     * 队伍密码
     */
    private String teamPassword;

    /**
     * 队伍最大人数
     */
    private Integer maxNum;

    /**
     *  队伍过期时间
     */
    private Date expireTime;

    /**
     * 队伍状态   0 公开 1私人 2加密
     */
    private Integer teamStatus;

    /**
     *  创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date updateTime;

    /**
     *  是否删除
     */
    @TableLogic
    private Byte isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}