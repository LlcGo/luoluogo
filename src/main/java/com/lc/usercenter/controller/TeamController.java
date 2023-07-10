package com.lc.usercenter.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lc.usercenter.common.BaseRespons;
import com.lc.usercenter.common.ErrorCode;
import com.lc.usercenter.common.ResponsUtil;
import com.lc.usercenter.exception.BusinessException;
import com.lc.usercenter.model.domain.Team;
import com.lc.usercenter.model.domain.User;

import com.lc.usercenter.model.domain.UserTeam;
import com.lc.usercenter.model.dto.TeamQuery;
import com.lc.usercenter.model.requset.*;
import com.lc.usercenter.model.vo.TeamVo;
import com.lc.usercenter.service.TeamService;
import com.lc.usercenter.service.UserService;
import com.lc.usercenter.service.UserTeamService;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import sun.dc.pr.PRError;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.lc.usercenter.contact.UserContant.USER_LOGIN_STATE;

/**
 * @Author Lc
 * @Date 2023/6/28
 * @Description
 */
@RestController
public class TeamController {

    @Resource
    private UserService userService;

    @Resource
    private TeamService teamService;

    @Resource
    private UserTeamService userTeamService;

    @PostMapping("/add")
    private BaseRespons<Long> addTeam(@RequestBody TeamAddRequest teamAddRequest, HttpServletRequest request){
        if(teamAddRequest == null){
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        Team team = new Team();
        BeanUtils.copyProperties(teamAddRequest,team);
        boolean save = teamService.saveTeam(team,request);
        if(!save){
            throw new BusinessException(ErrorCode.SYSTEM_ERR,"插入关系表出错");
        }
        return ResponsUtil.success(team.getTeamId());
    }


    @PostMapping("/update")
    private BaseRespons<Boolean> update(@RequestBody TeamUpdateRequest teamUpdateRequest,HttpServletRequest request){
        if(teamUpdateRequest == null){
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        boolean result = teamService.updateTeam(teamUpdateRequest,loginUser);
        if(!result){
            throw new BusinessException(ErrorCode.SYSTEM_ERR);
        }
        return ResponsUtil.success(true);
    }

    @GetMapping("/select/{id}")
    private BaseRespons<Team> selectOne(@PathVariable long id){
        if(id <= 0){
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        Team team = teamService.getById(id);
        if(team == null){
            throw new BusinessException(ErrorCode.NULL_ERROR,"没有关于这个id的数据");
        }
        return ResponsUtil.success(team);
    }

    //根据用户查看自己加了多少队伍
//    @GetMapping("/list")
//    private BaseRespons<List<Team>> listTeams(TeamQuery teamQuery){
//        if(teamQuery == null){
//            throw new BusinessException(ErrorCode.PARAMS_ERROR);
//        }
//        Team team = new Team();
//        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
//        BeanUtils.copyProperties(team,teamQuery);
//        List<Team> teamList = teamService.list(queryWrapper);
//        if(teamList == null){
//            throw new BusinessException(ErrorCode.PARAMS_ERROR,"查询数据为null");
//        }
//        return ResponsUtil.success(teamList);
//    }

    //根据相对的信息查询 队伍 并且这些队伍里面的人数量
    @GetMapping("/list")
    private BaseRespons<List<TeamVo>> listTeams(TeamQuery teamQuery, HttpServletRequest request){
        if(teamQuery == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean isAdmin = userService.isAdmin(request);
        List<TeamVo> teamList = teamService.listTeams(teamQuery,isAdmin);
        if(teamList == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"查询数据为null");
        }
        teamService.intoHasJoin(request,teamList);
        return ResponsUtil.success(teamList);
    }



    @GetMapping("/list/page")
    private BaseRespons<Page<Team>> page(TeamQuery teamQuery){
        if(teamQuery == null){
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        long pageSize = teamQuery.getPageSize();
        long pageNum = teamQuery.getPageNum();
        if(pageSize <= 0 || pageNum <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team team = new Team();
        BeanUtils.copyProperties(teamQuery,team);
        Page<Team> teamPage = new Page<>(pageNum,pageSize);
        //TODO
        QueryWrapper<Team> teamQueryWrapper = new QueryWrapper<>(team);
        Page<Team> pageTeam = teamService.page(teamPage,teamQueryWrapper);
        if(pageTeam == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"没有查到数据");
        }
        return ResponsUtil.success(pageTeam);
    }

    @PostMapping("/join")
    private BaseRespons<Boolean> joinTeam(@RequestBody JoinTeamRequset joinTeamRequset,HttpServletRequest request){
        if(joinTeamRequset == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        Boolean result = teamService.joinTeam(joinTeamRequset,loginUser);
        return ResponsUtil.success(result);
    }

    @PostMapping("/quit")
    private BaseRespons<Boolean> quitTeam(@RequestBody QuitTeamRequest quitTeamRequest, HttpServletRequest request){
        if(quitTeamRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        Boolean result = teamService.quitTeam(quitTeamRequest,loginUser);
        return ResponsUtil.success(result);
    }

    @PostMapping("/delete")
    private BaseRespons<Boolean> deleteTeam(@RequestBody DeleteTeamRequest deleteTeamRequest, HttpServletRequest request){
        if(deleteTeamRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long id = deleteTeamRequest.getId();
        if(id <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        boolean result = teamService.deleteTeam(id,loginUser);
        if(!result){
            throw new BusinessException(ErrorCode.SYSTEM_ERR);
        }
        return ResponsUtil.success(true);
    }

    @GetMapping("/list/my/team")
    private BaseRespons<List<TeamVo>> listMyCreatTeams(TeamQuery teamQuery, HttpServletRequest request){
        if(teamQuery == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        teamQuery.setUserId(loginUser.getId());
        List<TeamVo> teamList = teamService.listTeams(teamQuery,true);
        if(teamList == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"查询数据为null");
        }
        teamService.intoHasJoin(request,teamList);
        return ResponsUtil.success(teamList);
    }

    @GetMapping("/list/join/team")
    private BaseRespons<List<TeamVo>> listMyJoinTeams(TeamQuery teamQuery, HttpServletRequest request){
        if(teamQuery == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", loginUser.getId());
        List<UserTeam> myJoinTeamList = userTeamService.list(queryWrapper);
        ArrayList<Long> idLists = new ArrayList<Long>(myJoinTeamList.stream().collect(Collectors.groupingBy(UserTeam::getUserId)).keySet());
        teamQuery.setIdList(idLists);
        List<TeamVo> teamList = teamService.listTeams(teamQuery,true);
        if(teamList == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"查询数据为null");
        }
        teamService.intoHasJoin(request,teamList);
        return ResponsUtil.success(teamList);
    }



}
