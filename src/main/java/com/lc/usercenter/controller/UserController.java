package com.lc.usercenter.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lc.usercenter.common.BaseRespons;
import com.lc.usercenter.exception.BusinessException;
import com.lc.usercenter.common.ErrorCode;
import com.lc.usercenter.common.ResponsUtil;
import com.lc.usercenter.model.domain.User;
import com.lc.usercenter.model.requset.UserLoginRequest;
import com.lc.usercenter.model.requset.UserRegisterRequest;
import com.lc.usercenter.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.lc.usercenter.contact.UserContant.ADMIN_ROLE;
import static com.lc.usercenter.contact.UserContant.USER_LOGIN_STATE;

/**
 * @Author Lc
 * @Date 2023/4/11
 * @Description
 */
@RestController
@RequestMapping("/user")
@Slf4j
//@CrossOrigin(origins = {"http://127.0.0.1:5173"})
public class UserController {

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private UserService userService;

    @PostMapping("/register")
    public BaseRespons<Long> register(@RequestBody UserRegisterRequest registerRequest){
        if(registerRequest == null){
            return null;
        }
        String confirmPassword = registerRequest.getConfirmPassword();
        String userAccount = registerRequest.getUserAccount();
        String userPassword = registerRequest.getUserPassword();
        String plantCode = registerRequest.getPlantCode();
        if(StringUtils.isAnyBlank(confirmPassword,userAccount,userPassword,plantCode)){
            return null;
        }
        long result = userService.register(userAccount, userPassword, confirmPassword, plantCode);
        return ResponsUtil.success(result);
    }

    @PostMapping("/login")
    public BaseRespons<User> Login(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request){
        if(userLoginRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"参数为空");
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if(StringUtils.isAnyBlank(userAccount,userPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"参数为空");
        }
        User user = userService.userLogin(userAccount, userPassword, request);
        return ResponsUtil.success(user);
    }

    @PostMapping("/outLogin")
    public BaseRespons<Integer> outLogin(HttpServletRequest request){
        if(request == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"参数为空");
        }
        int result = userService.outLogin(request);
        return ResponsUtil.success(result);
    }

    @GetMapping("/current")
    public BaseRespons<User> currentUser(HttpServletRequest req){
        Object o = req.getSession().getAttribute(USER_LOGIN_STATE);
        User currUser = (User)o;
        if(currUser == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"参数为空");
        }
        Long userId = currUser.getId();
        User user = userService.getById(userId);
        User safeUser = userService.getSafeUser(user);
        return ResponsUtil.success(safeUser);
    }

    @PostMapping("/update")
    public BaseRespons<Integer> updateByUser(@RequestBody User user,HttpServletRequest req){
        if(user == null){
            throw  new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(req);
        if(loginUser == null){
            throw new BusinessException(ErrorCode.NOT_ROLE);
        }
        return ResponsUtil.success(userService.updateByUser(user, loginUser));
    }

    @GetMapping("/search")
    public BaseRespons<List<User>> searchUsers(String name, HttpServletRequest req){
        if (!userService.isAdmin(req)) {
            throw new BusinessException(ErrorCode.NOT_ROLE,"您不是管理员");
        }

        QueryWrapper<User> wrapper = new QueryWrapper<>();
        if(StringUtils.isNotBlank(name)){
            wrapper.like("userName",name);
        }
        List<User> userList = userService.list(wrapper);
        //用户脱敏
        List<User> result = userList.stream()
                .map(user -> userService.getSafeUser(user))
                .collect(Collectors.toList());
        return ResponsUtil.success(result);
    }


    @GetMapping("/recommend")
    public BaseRespons<Page<User>> recommendUsers(long pageNum,long pageSize,HttpServletRequest req){
//        if(recom_users != null){
//            List<User> userList1 = recom_users.stream().collect(Collectors.toList());
//
//        }
        User loginUser = userService.getLoginUser(req);
        String redisKey =  String.format("LLG:RECOMMEND:%s",loginUser.getId());
        ValueOperations valueOperations = redisTemplate.opsForValue();
        Page<User> userPage= (Page<User>)valueOperations.get(redisKey);
        if(userPage != null){
            return ResponsUtil.success(userPage);
        }
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        userPage = userService.page(new Page<>(pageNum,pageSize),wrapper);
//        Map<Long, User> userMap = userList.getRecords().stream().collect(Collectors.toMap(
//                User::getId,
//                user -> {
//                    return user;
//                },
//                (oldVlaue, newValue) -> newValue
//        ));
//        redisTemplate.opsForHash().putAll("RECOM_USERS",userMap);
        try {
            valueOperations.set(redisKey,userPage,10000, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("red set err",e);
        }
        return ResponsUtil.success(userPage);
    }

    @GetMapping("/searchUserByTags")
    public BaseRespons<List<User>> searchUserByTags(@RequestParam(required = false) List<String> tagsName){
         if(tagsName == null) {
             throw new BusinessException(ErrorCode.PARAMS_ERROR);
         }
        List<User> users = userService.searchUserByTageName(tagsName);
        if(ObjectUtils.isEmpty(users)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"查询的参数为null");
        }
        return ResponsUtil.success(users);
    }

    @PostMapping("/delete")
    public BaseRespons<Boolean> deleteUser(@RequestBody Long id , HttpServletRequest req){
        if(!userService.isAdmin(req)){
            throw new BusinessException(ErrorCode.NOT_ROLE,"您不是管理员");
        };
        if(id < 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"没有这个id");
        }
        boolean result = userService.removeById(id);
        return ResponsUtil.success(result);
    }




}

