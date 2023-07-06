package com.lc.usercenter.model.requset;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author Lc
 * @Date 2023/4/11
 * @Description
 */
@Data
public class UserLoginRequest implements Serializable {
    private static final long serialVersionUID = -1528752391661171552L;
    private String userAccount;
    private String userPassword;
}
