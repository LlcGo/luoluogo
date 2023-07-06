package com.lc.usercenter.model.requset;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author Lc
 * @Date 2023/7/1
 * @Description
 */
@Data
public class JoinTeamRequset implements Serializable {
    private static final long serialVersionUID = -7300751881855374756L;
    /**
     * 队伍id
     */
    private Long teamId;


    /**
     * 队伍密码
     */
    private String teamPassword;
}
