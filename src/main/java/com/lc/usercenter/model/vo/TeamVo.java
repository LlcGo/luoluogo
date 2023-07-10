package com.lc.usercenter.model.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import io.swagger.models.auth.In;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @Author Lc
 * @Date 2023/6/30
 * @Description
 */
@Data
public class TeamVo implements Serializable {
    private static final long serialVersionUID = -6388463129907300734L;
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
     * 创建人
     */
    private UserVo currentUser;

    /**
     * 加入队伍的人数
     */
    private Integer isJoinTeamUsers;

    /**
     * 是否加入队伍
     */
    private boolean isJoinTeam = false;
}
