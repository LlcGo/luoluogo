package com.lc.usercenter.model.requset;

import lombok.Data;

/**
 * @Author Lc
 * @Date 2023/7/2
 * @Description 退出队伍请求体
 */
@Data
public class QuitTeamRequest {
    /**
     * 队伍id
     */
    private Long teamId;

}
