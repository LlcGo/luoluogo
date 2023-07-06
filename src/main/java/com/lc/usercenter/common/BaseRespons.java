package com.lc.usercenter.common;

import lombok.Data;

/**
 * @Author Lc
 * @Date 2023/6/11
 * @Description 返回类封装
 */
@Data
public class BaseRespons<T> {
    private String message;
    private T date;
    private Integer code;
    private String description;

    public BaseRespons(String message, T date, Integer code,String description) {
        this.message = message;
        this.date = date;
        this.code = code;
        this.description = description;
    }

    public BaseRespons(String message, T date, Integer code) {
        this.message = message;
        this.date = date;
        this.code = code;
    }

    public BaseRespons(T date,Integer code,String message) {
       this(message,date,code,"");
    }

    public BaseRespons(T date,Integer code){
        this("",date,code,"");
    }

    public BaseRespons(ErrorCode errorCode){
        this(errorCode.getMessage(),null,errorCode.getCode(),errorCode.getDescription());
    }


}
