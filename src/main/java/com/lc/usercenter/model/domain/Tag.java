package com.lc.usercenter.model.domain;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 标签表
 * @TableName tag
 */
@TableName(value ="tag")
@Data
public class Tag implements Serializable {
    /**
     * 主键递增
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 标签名
     */
    private String tageName;

    /**
     * 创建标签用户的标签
     */
    private Long userId;

    /**
     * 用于分类的父标签
     */
    private Integer parentId;

    /**
     * 性别
     */
    private Integer gender;

    /**
     * 是否为父标签
     */
    private Integer isParent;

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

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}