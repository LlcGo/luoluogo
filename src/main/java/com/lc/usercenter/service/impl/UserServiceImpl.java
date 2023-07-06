package com.lc.usercenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lc.usercenter.exception.BusinessException;
import com.lc.usercenter.common.ErrorCode;
import com.lc.usercenter.model.domain.User;
import com.lc.usercenter.service.UserService;
import com.lc.usercenter.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.lc.usercenter.contact.UserContant.ADMIN_ROLE;
import static com.lc.usercenter.contact.UserContant.USER_LOGIN_STATE;

/**
 *
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

    @Resource
    private UserMapper userMapper;
    /**
     * 盐值，混淆密码
     */
    private static final String SALT= "LC";

    @Override
    public long register(String userAccount, String userPassword, String confirmPassword,String plantCode) {
        if(StringUtils.isAnyBlank(userAccount,userPassword,confirmPassword,plantCode)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"输入的数据少了");
        }
        if(userAccount.length() < 4){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账户少于4位");
        }
        if(userPassword.length() < 8){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码长度短语8位");
        }
        if(plantCode.length() > 5){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户编码大于5位");
        }

        //判断账户是否含有特殊字符
        String regEx = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(regEx).matcher(userAccount);
        if(matcher.find()){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"输入了特殊字符");
        }

        if(!userPassword.equals(confirmPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"俩次密码不同");
        }

        //查询数据库中是否有重复账户
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUserAccount,userAccount);
        Long count = this.baseMapper.selectCount(queryWrapper);
        if(count > 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账户重复");
        }

        //用户编码重复
        queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getPlantCode,plantCode);
        count = this.baseMapper.selectCount(queryWrapper);
        if(count > 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户编码重复");
        }

        //对密码进行加密存入数据库
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());

        //存入数据库
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        user.setPlantCode(plantCode);
        boolean saveResult = this.save(user);
        if(!saveResult){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"存入数据库失败");
        }
        return user.getId();
    }

    @Override
    public User userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        if(StringUtils.isAnyBlank(userAccount,userPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"少传入参数");
        }
        if(userAccount.length() < 4){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账户长度小于4");
        }
        if(userPassword.length() < 8){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码少于8");
        }

        //判断账户是否含有特殊字符
        String regEx = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(regEx).matcher(userAccount);
        if(matcher.find()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"有特殊字符");
        }


        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());

        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUserAccount,userAccount);
        queryWrapper.eq(User::getUserPassword,encryptPassword);
        User user = this.getOne(queryWrapper);
        if(user == null){
            log.info("user login failed, userAccount cannot match userPassword");
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户不存在");
        }

        //用户脱敏
        User safeUser = getSafeUser(user);
        //存入session
        request.getSession().setAttribute(USER_LOGIN_STATE,safeUser);
        return safeUser;
    }

    @Override
    public User getSafeUser(User user){
        if(user == null){
            return null;
        }
        User safeUser = new User();
        //这里新增字段别忘了加
        safeUser.setPlantCode(user.getPlantCode());
        safeUser.setId(user.getId());
        safeUser.setUserName(user.getUserName());
        safeUser.setUserAccount(user.getUserAccount());
        safeUser.setAvatarUrl(user.getAvatarUrl());
        safeUser.setGender(user.getGender());
        safeUser.setPhone(user.getPhone());
        safeUser.setEmail(user.getEmail());
        safeUser.setProfile(user.getProfile());
        safeUser.setUserRole(user.getUserRole());
        safeUser.setUserStatus(user.getUserStatus());
        safeUser.setCreateTime(user.getCreateTime());
        safeUser.setTags(user.getTags());
        return safeUser;
    }

    @Override
    public int outLogin(HttpServletRequest req) {
        req.getSession().removeAttribute(USER_LOGIN_STATE);
        return 1;
    }

    /**
     * 根据标签名查询用户  (内存过滤)
     * @param listTageName
     * @return
     */
    @Override
    public List<User> searchUserByTageName(List<String> listTageName){
        if(CollectionUtils.isEmpty(listTageName)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
//        for (String tageName: listTageName) {
//            queryWrapper.like("tags",tageName);
//        }
        List<User> userList = userMapper.selectList(null);
        Gson gson = new Gson();
        //查出来的所有用户
        List<User> user = userList.stream().filter((user1 -> {
            //每一个user里面的标签
            String tags = user1.getTags();
            if (tags == null) {
                return false;
            }
            //把标签转换为set集合
            Set<String> tagList = gson.fromJson(tags,
                    new TypeToken<Set<String>>() {}.getType());
            //查出来的标签的集合
            tagList = Optional.ofNullable(tagList).orElse(new HashSet<>());
            for (String tag : listTageName) {
                if (!tagList.contains(tag)) {
                    return false;
                }
            }
            return true;
        })).collect(Collectors.toList()).stream().map(this::getSafeUser).collect(Collectors.toList());
        return user;
//        List<User> users = userMapper.selectList(queryWrapper);
//        return users.stream().map(this::getSafeUser).collect(Collectors.toList());
    }

    @Override
    public int updateByUser(User user, User loginUser) {
        //判断传入的用户是不是有id
        long userId = user.getId();
        if(userId <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //不是管理员,并且当前修改的用户id 不是 他自己的用户id
        if(!isAdmin(loginUser) && userId != loginUser.getId()){
            throw new BusinessException(ErrorCode.NOT_ROLE);
        }
        User oldUser = userMapper.selectById(userId);
        //是否有这个用户
        if(oldUser == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        int result = userMapper.updateById(user);
        if(result <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        return result;
    }

    @Override
    public boolean isAdmin(HttpServletRequest req) {
            Object o = req.getSession().getAttribute(USER_LOGIN_STATE);
            User user = (User)o;
            return user != null && user.getUserRole() == ADMIN_ROLE;
    }

    @Override
    public boolean isAdmin(User user) {
        return user != null && user.getUserRole() == ADMIN_ROLE;
    }

    @Override
    public User getLoginUser(HttpServletRequest req) {
        if(req == null){
            return null;
        }
        return (User)req.getSession().getAttribute(USER_LOGIN_STATE);
    }

}




