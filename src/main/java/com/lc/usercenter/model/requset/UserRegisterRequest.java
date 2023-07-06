package com.lc.usercenter.model.requset;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author Lc
 * @Date 2023/4/11
 * @Description
 */
@Data
public class UserRegisterRequest implements Serializable {

    private static final long serialVersionUID = 4392503567601376478L;

    private String userAccount;
    private String userPassword;
    private String confirmPassword;
    //这里新加的字段别忘了加
    private String plantCode;

}
