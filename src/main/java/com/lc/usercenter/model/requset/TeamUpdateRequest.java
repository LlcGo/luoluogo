package com.lc.usercenter.model.requset;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

import java.util.Date;

/**
 * @Author Lc
 * @Date 2023/7/1
 * @Description
 */
@Data
public class TeamUpdateRequest {
    /**
     * 队伍id
     */
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
     *  队伍过期时间
     */
    private Date expireTime;

    /**
     * 队伍状态   0 公开 1私人 2加密
     */
    private Integer teamStatus;


}
