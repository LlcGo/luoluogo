package com.lc.usercenter.common;

/**
 * @Author Lc
 * @Date 2023/6/11
 * @Description 返回类封装的方法
 */
public class ResponsUtil {
    public static <T> BaseRespons<T> success(T data){
        return new BaseRespons<T>("ok",data,0);
    }


//    String message, T date, Integer code,String description
    public static BaseRespons error(ErrorCode errorCode){
        return new BaseRespons(errorCode);
    }

    /**
     * 全局返回系统失败
     * @param errorCode
     * @param message
     * @param description
     * @return
     */
    public static BaseRespons error(ErrorCode errorCode, String message, String description){
        return new BaseRespons(message,null,errorCode.getCode(),description);
    }

    /**
     * 全局返回失败
     * @param code
     * @param message
     * @param description
     * @return
     */
    public static BaseRespons error(Integer code, String message, String description){
        return new BaseRespons(message,null,code,description);
    }
}
