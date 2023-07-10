package com.lc.usercenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lc.usercenter.common.ErrorCode;
import com.lc.usercenter.exception.BusinessException;
import com.lc.usercenter.model.domain.Team;
import com.lc.usercenter.model.domain.User;
import com.lc.usercenter.model.domain.UserTeam;
import com.lc.usercenter.model.dto.TeamQuery;
import com.lc.usercenter.model.emus.TeamStatusEnums;
import com.lc.usercenter.model.requset.JoinTeamRequset;
import com.lc.usercenter.model.requset.QuitTeamRequest;
import com.lc.usercenter.model.requset.TeamUpdateRequest;
import com.lc.usercenter.model.vo.TeamVo;
import com.lc.usercenter.model.vo.UserVo;
import com.lc.usercenter.service.TeamService;
import com.lc.usercenter.mapper.TeamMapper;
import com.lc.usercenter.service.UserService;
import com.lc.usercenter.service.UserTeamService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.lc.usercenter.common.ErrorCode.NOT_ROLE;
import static com.lc.usercenter.common.ErrorCode.PARAMS_ERROR;

/**
 *
 */
@Service
@Slf4j
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team>
    implements TeamService{

    @Resource
    private UserService userService;

    @Resource
    private UserTeamService userTeamService;

    @Resource
    private RedissonClient redissonClient;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveTeam(Team team, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        if(loginUser == null){
            throw new BusinessException(NOT_ROLE,"请用户登录");
        }
        String teamName = team.getTeamName();
        if(StringUtils.isBlank(teamName)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        int maxNum = Optional.ofNullable(team.getMaxNum()).orElse(0) ;
        if(maxNum <=1 || maxNum > 20){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"设置人数因该大于1小于20");
        }
        String description = team.getDescription();
        if(description.length() > 200){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"设置队伍描述应该小于200");
        }
        int teamStatus = Optional.ofNullable(team.getTeamStatus()).orElse(0);
        TeamStatusEnums enumBystatus = TeamStatusEnums.getEnumBystatus(teamStatus);
        if(enumBystatus == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"设置队伍状态出错");
        }
        String teamPassword = team.getTeamPassword();
        if(TeamStatusEnums.ENCIPHER.equals(enumBystatus) && StringUtils.isBlank(teamPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请输入密码");
        }
        //如果超时时间大于当前时间
        if(new Date().after(team.getExpireTime())){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"设置时间应该大于当前时间");
        }
        //查询用户自己有多少个队伍
        long userId = loginUser.getId();
        LambdaQueryWrapper<Team> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Team::getUserId,userId);
        long count = this.count(queryWrapper);
        if(count > 5){
            throw new BusinessException(NOT_ROLE,"创建队伍已超过数量");
        }
        //插入数据前别忘记了给这个队伍设置队伍的id
        team.setUserId(userId);
        boolean TeamSave = this.save(team);
        if(!TeamSave){
            throw new BusinessException(ErrorCode.SYSTEM_ERR);
        }

        UserTeam userTeam = new UserTeam();
        userTeam.setTeamId(team.getTeamId());
        userTeam.setUserId(userId);
        return userTeamService.save(userTeam);
    }

    @Override
    public List<TeamVo> listTeams(TeamQuery teamQuery, boolean isAdmin) {
        LambdaQueryWrapper<Team> queryWrapper = new LambdaQueryWrapper<>();
        //组合sql
        if(teamQuery != null){
            Long teamId = teamQuery.getTeamId();
            queryWrapper.eq(teamId != null && teamId > 0 , Team::getTeamId,teamId);

            String searText = teamQuery.getSearText();
            if(StringUtils.isNotBlank(searText)){
                queryWrapper.and(qw-> qw.like(Team::getTeamName,searText).or().like(Team::getDescription,searText));
            }

            String teamName = teamQuery.getTeamName();
            queryWrapper.like(StringUtils.isNotBlank(teamName),Team::getTeamName,teamName);

            String description = teamQuery.getDescription();
            queryWrapper.like(StringUtils.isNotBlank(description),Team::getDescription,description);

            Integer maxNum = teamQuery.getMaxNum();
            queryWrapper.eq(maxNum != null && maxNum > 1,Team::getMaxNum,maxNum);

            Date expireTime = teamQuery.getExpireTime();
            queryWrapper.gt(expireTime != null,Team::getExpireTime,expireTime);

            Long userId = teamQuery.getUserId();
            queryWrapper.eq(userId != null && userId > 0, Team::getUserId,userId );


            Integer teamStatus = teamQuery.getTeamStatus();
            TeamStatusEnums enumBystatus = TeamStatusEnums.getEnumBystatus(teamStatus);
            if(enumBystatus == null){
                enumBystatus = TeamStatusEnums.PUBLICE;
            }
            if(!isAdmin && enumBystatus.equals(TeamStatusEnums.ENCIPHER)){
                throw new BusinessException(NOT_ROLE);
            }
            queryWrapper.eq(Team::getTeamStatus,enumBystatus.getStatus());

        }

        queryWrapper.and(qw -> qw.gt(Team::getExpireTime,new Date()).or().isNull(Team::getExpireTime));

        //TODO
        List<Team> listTeams = this.list(queryWrapper);
        if(CollectionUtils.isEmpty(listTeams)){
            return new ArrayList<>();
        }
        ArrayList<TeamVo> teamVosList = new ArrayList<>();
        for (Team team : listTeams) {
            Long userId = team.getUserId();
            if(userId == null){
                continue;
            }
            User user = userService.getById(userId);
            TeamVo teamVo = new TeamVo();
            BeanUtils.copyProperties(team,teamVo);
            //脱敏信息
            UserVo userVo = new UserVo();
            BeanUtils.copyProperties(user,userVo);
            teamVo.setCurrentUser(userVo);
            teamVosList.add(teamVo);
        }
        return teamVosList;
    }

    @Override
    public boolean updateTeam(TeamUpdateRequest teamUpdateRequest, User loginUser) {
        //1.判断传入是否为空
        if(teamUpdateRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //2.判断当前队伍是否存在
        Long teamId = teamUpdateRequest.getTeamId();
        if(teamId == null || teamId <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"修改的队伍不存在");
        }
        Team oldTeam = this.getById(teamId);
        if(oldTeam == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍不存在");
        }
        //3.判断当前用户不是创建用户的人，也不是管理员 那就直接抛异常
        Long teamUserId = oldTeam.getUserId();
        long currentUserId = loginUser.getId();
        if(teamUserId !=  currentUserId && !userService.isAdmin(loginUser)){
            throw new BusinessException(NOT_ROLE);
        }
        //4.如果队伍状态是改为加密那么，必须设置密码
        Integer teamStatus = teamUpdateRequest.getTeamStatus();
        TeamStatusEnums enumBystatus = TeamStatusEnums.getEnumBystatus(teamStatus);
        if(enumBystatus == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String teamPassword = teamUpdateRequest.getTeamPassword();
        if(TeamStatusEnums.ENCIPHER.getStatus() == teamStatus && StringUtils.isBlank(teamPassword)){
           throw new BusinessException(ErrorCode.PARAMS_ERROR,"请设置密码");
        }
        //5.更新队伍
        Team team = new Team();
        BeanUtils.copyProperties(teamUpdateRequest,team);
        return this.updateById(team);
    }

    @Override
    public Boolean joinTeam(JoinTeamRequset joinTeamRequset, User loginUser) {
        //1.判断传入参数是否为null
        if (joinTeamRequset == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //2.判断队伍是否存在 ，只能加入未过期，未满的队伍
        Long teamId = joinTeamRequset.getTeamId();
        if (teamId == null || teamId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "加入的队伍不存在");
        }
        Team joinTeam = this.getById(teamId);
        Date expireTime = joinTeam.getExpireTime();
        if (new Date().after(expireTime)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "加入的队伍已经超时");
        }
        long currentUserId = loginUser.getId();
//        QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
//        userTeamQueryWrapper.eq("userId",currentUserId);
//        long count1 = userTeamService.count(userTeamQueryWrapper);
        Integer maxNum = joinTeam.getMaxNum();
        //3.判断加入的队伍是否已经超过5个
        if (maxNum == null || maxNum > 5) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "加入的队伍人数已满");
        }
        //5.判断队伍状态
        //（1）有密码 如果队伍加密判断密码是否正确
        Integer teamStatus = joinTeam.getTeamStatus();
        TeamStatusEnums enumBystatus = TeamStatusEnums.getEnumBystatus(teamStatus);
        if (enumBystatus == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "该状态不存在");
        }
        String teamPassword = joinTeamRequset.getTeamPassword();
        Optional.ofNullable(teamPassword).orElse("没有设置密码");
        if (TeamStatusEnums.ENCIPHER.equals(enumBystatus) && !teamPassword.equals(joinTeam.getTeamPassword())) {
            throw new BusinessException(PARAMS_ERROR, "请输入正确密码");
        }
        //（2）如果是私密不能加入
        if (TeamStatusEnums.PRIVER.equals(enumBystatus)) {
            throw new BusinessException(PARAMS_ERROR, "队伍为私密,不允许加入");
        }

        RLock lock = redissonClient.getLock("llg:joinTeam");
        //time,多少秒后重试 leaseTime 锁过期时间 -1 才能触发看门狗机制
        while (true) {
            try {
            if (lock.tryLock(0, -1, TimeUnit.MILLISECONDS)) {
                //4不能重复加入已加入的队伍
                QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("teamId", joinTeam.getTeamId()).eq("userId", currentUserId);
                long count = userTeamService.count(queryWrapper);
                if (count != 0) {
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "不能重复加入已经加入的队伍");
                }

                //加入队伍的数量不能超过5个
                queryWrapper = new QueryWrapper<UserTeam>();
                queryWrapper.eq("userId", currentUserId);
                long userJoinTeamCount = userTeamService.count(queryWrapper);
                if (userJoinTeamCount > 5) {
                    throw new BusinessException(NOT_ROLE, "加入的队伍不能超过5个");
                }

                //6.修改队伍  增加队伍的人数
                UpdateWrapper<Team> updateWrapper = new UpdateWrapper<>();
                updateWrapper.set("maxNum", joinTeam.getMaxNum() + 1);
                updateWrapper.eq("teamId", joinTeam.getTeamId());
                this.update(updateWrapper);
                //7.在关系表添加一个新的记录
                UserTeam userTeam = new UserTeam();
                userTeam.setUserId(currentUserId);
                userTeam.setTeamId(joinTeam.getTeamId());
                userTeam.setJionTime(new Date());
                return userTeamService.save(userTeam);
            }
        } catch(InterruptedException e){
            log.info("PrecaCheRedis error", e);
            return false;
        } finally{
            //判断是否当前是自己的锁
            //释放掉自己的锁
            //一定要放在这里无论如何都要执行
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

}





    @Override
    public Boolean quitTeam(QuitTeamRequest quitTeamRequest, User loginUser) {
        //1.参数校验是否为空
        if (quitTeamRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //2.队伍是否存在
        Long teamId = quitTeamRequest.getTeamId();
        if(teamId == null || teamId <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team team = this.getById(teamId);
        if(team == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍不存在");
        }
        //3.校验是否加入队伍
        long currentId = loginUser.getId();
        UserTeam userTeam = new UserTeam();
        userTeam.setUserId(currentId);
        userTeam.setTeamId(teamId);
        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>(userTeam);
        long count = userTeamService.count(queryWrapper);
        if(count <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"您还没有加入该队伍");
        }
        //4.退出队伍的情况
        //   （1）如果队伍只有一个人那就直接解散队伍  删除队伍，并且删除关系表
        QueryWrapper<UserTeam> countWrapper = new QueryWrapper<UserTeam>();
        countWrapper.eq("teamId",teamId);
        long teamUserCount = userTeamService.count(countWrapper);
        if(teamUserCount == 1){
            boolean result = this.removeById(teamId);
        }else {
            //   （2）如果队伍人数大于1个人，就在队伍表里面查出id 第二条的数据 （根据id，最早的为队长，第二个为顺位的人）并且删除关系表 并且maxnum-1
            //如果当前用户的id 和 team的用户id是一致的那就是队长
            if(currentId == team.getUserId()) {
                QueryWrapper<UserTeam> selectListWrapper = new QueryWrapper<UserTeam>();
                selectListWrapper.eq("teamId", teamId).last("order by tauId asc limit 2");
                List<UserTeam> userTeamList = userTeamService.list(selectListWrapper);
                if (CollectionUtils.isEmpty(userTeamList) || userTeamList.size() < 2) {
                    throw new BusinessException(ErrorCode.SYSTEM_ERR, "没有俩条队伍 或者 队伍为空关系表没有数据");
                }
                //下一个房主
                UserTeam nextUser = userTeamList.get(1);
                Long nextUserId = nextUser.getUserId();
                if (nextUserId == null || nextUserId <= 0) {
                    throw new BusinessException(ErrorCode.SYSTEM_ERR, "该id不存在");
                }
                //修改房主
                Team updateTeam = new Team();
                updateTeam.setTeamId(teamId);
                updateTeam.setUserId(nextUserId);
                boolean result = this.updateById(updateTeam);
                if (!result) {
                    throw new BusinessException(ErrorCode.SYSTEM_ERR, "删除队伍失败");
                }
            }
            //大于一个人的情况最后肯定是要减少队伍里的人数 maxnum -1
            Team decTeamNum = new Team();
            decTeamNum.setMaxNum(team.getMaxNum() - 1);
            decTeamNum.setTeamId(teamId);
            boolean result = this.updateById(decTeamNum);
            if(!result){
                throw new BusinessException(ErrorCode.SYSTEM_ERR,"修改人数失败");
            }
        }
        // 无论如何都要 删除关系表
        return userTeamService.remove(queryWrapper);
    }

    @Override
    public boolean deleteTeam(long id, User loginUser) {
        //1.校验传入参数
        if(id <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //2.校验是否有这个队伍
        Team team = this.getById(id);
        if(team == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍不存在");
        }
        //3.校验是否是队伍的队长
        Long teamUserId = team.getUserId();
        long currentUserId = loginUser.getId();
        if(teamUserId != currentUserId){
            throw new BusinessException(NOT_ROLE,"您不是队长，无权利解散队伍");
        }
        //4.删除所有关联队伍的关系
        Long teamId = team.getTeamId();
        UserTeam userTeam = new UserTeam();
        userTeam.setTeamId(teamId);
        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>(userTeam);
        boolean remove = userTeamService.remove(queryWrapper);
        if(!remove){
            throw new BusinessException(ErrorCode.SYSTEM_ERR);
        }
        //5.删除队伍
        return this.removeById(teamId);
    }

    @Override
    public void intoHasJoin(HttpServletRequest request, List<TeamVo> teamList) {
        //当前查出来的队伍的id集合
        List<Long> resultTeamIdList = teamList.stream().map(TeamVo::getTeamId).collect(Collectors.toList());
        User loginUser = userService.getLoginUser(request);
        if(loginUser == null){
            throw  new BusinessException(ErrorCode.NOT_ROLE);
        }
        long userId = loginUser.getId();
        //根据id 和 现在查出来的 队伍 id 查出自己加入了那几个队伍
        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId",userId);
        queryWrapper.in("teamId",resultTeamIdList);
        Set<Long> joinTeamSet = userTeamService.list(queryWrapper).stream()
                .map(UserTeam::getTeamId)
                .collect(Collectors.toSet());
        //所有查出的队伍id是否存在 在 这个队伍的id中
        teamList.forEach(teamVo -> {
            boolean hasJoin = joinTeamSet.contains(teamVo.getTeamId());
            teamVo.setJoinTeam(hasJoin);
        });
        QueryWrapper<UserTeam> wrapper = new QueryWrapper<>();
        //查出这个队伍id加入的所有的人数
        wrapper.in("teamId",resultTeamIdList);
        List<UserTeam> list = userTeamService.list(wrapper);
        //long 用户id  list 所加的队伍的集合
        Map<Long, List<UserTeam>> collect = list.stream()
                .collect(Collectors.groupingBy(UserTeam::getTeamId));
        teamList.forEach(teamVo -> {
            teamVo.setIsJoinTeamUsers(collect.getOrDefault(teamVo.getTeamId(),new ArrayList<>()).size());
        });
    }
}




