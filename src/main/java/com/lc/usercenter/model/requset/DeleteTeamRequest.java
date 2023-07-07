package com.lc.usercenter.model.requset;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author Lc
 * @Date 2023/7/7
 * @Description
 */
@Data
public class DeleteTeamRequest implements Serializable {

    private static final long serialVersionUID = 7010581389282464192L;
    /**
     * 退出队伍id
     */
    private long id;
}
