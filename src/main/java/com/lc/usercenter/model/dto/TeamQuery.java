package com.lc.usercenter.model.dto;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.lc.usercenter.common.RequestPage;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * @Author Lc
 * @Date 2023/6/29
 * @Description
 */

@EqualsAndHashCode(callSuper = true)
@Data
public class TeamQuery extends RequestPage {
    /**
     * 队伍id
     */
    private Long teamId;

    /**
     * 前端传过来的文本信息
     */
    private String searText;
    /**
     * 队伍主人的id
     */
    private Long userId;

    /**
     * 队伍状态   0 公开 1私人 2加密
     */
    private Integer teamStatus;


    /**
     * 队伍名称
     */
    private String teamName;

    /**
     * 队伍最大人数
     */
    private Integer maxNum;

    /**
     * 队伍描述
     */
    private String description;

    /**
     *  队伍过期时间
     */
    private Date expireTime;


}
