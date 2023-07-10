package com.lc.usercenter.service;

import com.lc.usercenter.model.domain.Team;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lc.usercenter.model.domain.User;
import com.lc.usercenter.model.dto.TeamQuery;
import com.lc.usercenter.model.requset.JoinTeamRequset;
import com.lc.usercenter.model.requset.QuitTeamRequest;
import com.lc.usercenter.model.requset.TeamUpdateRequest;
import com.lc.usercenter.model.vo.TeamVo;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 *
 */
public interface TeamService extends IService<Team> {

    /**
     * 新增队伍
     * @param team
     * @param request
     * @return
     */
    boolean saveTeam(Team team, HttpServletRequest request);

    /**
     * 查询队伍
     * @param teamQuery
     * @param isAdmin
     * @return
     */
    List<TeamVo> listTeams(TeamQuery teamQuery, boolean isAdmin);

    /**
     * 修改队伍
     * @param teamUpdateRequest 修改的数据
     * @param loginUser 当前用户
     * @return
     */
    boolean updateTeam(TeamUpdateRequest teamUpdateRequest, User loginUser);

    /**
     * 添加队伍
     * @param joinTeamRequset
     * @param loginUser
     * @return
     */
    Boolean joinTeam(JoinTeamRequset joinTeamRequset, User loginUser);

    /**
     * 退出队伍
     * @param quitTeamRequest
     * @param loginUser
     * @return
     */
    Boolean quitTeam(QuitTeamRequest quitTeamRequest, User loginUser);

    /**
     * 解散队伍
     * @param id
     * @param loginUser
     * @return
     */
    boolean deleteTeam(long id, User loginUser);


    /**
     * 给当前查询出来的队伍赋值 是否加入 与 现在加入队伍的人数
     * @param request
     * @param teamList
     */
    void intoHasJoin(HttpServletRequest request, List<TeamVo> teamList);
}
