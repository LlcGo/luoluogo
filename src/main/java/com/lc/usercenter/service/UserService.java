package com.lc.usercenter.service;

import com.lc.usercenter.model.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static com.lc.usercenter.contact.UserContant.ADMIN_ROLE;
import static com.lc.usercenter.contact.UserContant.USER_LOGIN_STATE;

/**
 *
 */
public interface UserService extends IService<User> {


    /**
     * 注册用户
     * @param userAccount 账户
     * @param userPassword 密码
     * @param confirmPassword 校验密码
     * @return
     */
    long register(String userAccount, String userPassword, String confirmPassword,String plantCode);

    User userLogin(String userAccount, String userPassword, HttpServletRequest request);

    User getSafeUser(User user);

    int outLogin(HttpServletRequest req);

    /**
     * 根据标签查找用户
     * @param listTageName
     * @return
     */
    List<User> searchUserByTageName(List<String> listTageName);

    /**
     * 修改用户
     * @param user
     * @param loginUser
     * @return
     */
    int updateByUser(User user, User loginUser);


    /**
     * 是否为管理员 false 不是  true 是
     * @param req
     * @return
     */
    boolean isAdmin(HttpServletRequest req);

    /**
     * 根据用户判断是否是管理员
     * @param user 当前用户
     * @return
     */
    boolean isAdmin(User user);

    /**
     * 获取当前的是哪个用户登录
     */
    User getLoginUser(HttpServletRequest req);
}
