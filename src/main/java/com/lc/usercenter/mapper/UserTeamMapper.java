package com.lc.usercenter.mapper;

import com.lc.usercenter.model.domain.User;
import com.lc.usercenter.model.domain.UserTeam;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Entity com.lc.usercenter.model.domain.UserTeam
 */
public interface UserTeamMapper extends BaseMapper<UserTeam> {
     List<User> selectUsers(@Param("userId")long userId, @Param("teamUserId") long teamUserId);
}




