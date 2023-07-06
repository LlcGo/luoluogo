package com.lc.usercenter.common;

public enum ErrorCode {
    SUCCESS(0,"ok",""),
    PARAMS_ERROR(40000,"请求数据出错",""),
    NULL_ERROR(40001,"返回数据为空",""),
    NOT_LOGIN(40100,"未登录",""),
    NOT_ROLE(40100,"没有权限",""),
    SYSTEM_ERR(50000,"系统错误","");

    /**
     * 状态信息码
     */
    private String message;

    /**
     *状态码
     */
    private Integer code;

    /**
     *状态详情码
     */
    private String description;

    ErrorCode(Integer code, String message,String description) {
        this.message = message;
        this.code = code;
        this.description = description;
    }

    public String getMessage() {
        return message;
    }

    public Integer getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
