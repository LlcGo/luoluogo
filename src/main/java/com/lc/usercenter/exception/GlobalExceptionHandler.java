package com.lc.usercenter.exception;

import com.lc.usercenter.common.BaseRespons;
import com.lc.usercenter.common.ErrorCode;
import com.lc.usercenter.common.ResponsUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @Author Lc
 * @Date 2023/6/11
 * @Description
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public BaseRespons businessExceptionHandler(BusinessException e){
        log.error("BusinessException：{}",e.getMessage());
       return ResponsUtil.error(e.getCode(),e.getMessage(),e.getDescription());
    }

    @ExceptionHandler(RuntimeException.class)
    public BaseRespons runtimeExceptionHandler(RuntimeException e){
        log.error("RuntimeException：{}" + e.getMessage());
        return ResponsUtil.error(ErrorCode.SYSTEM_ERR,e.getMessage(),"");
    }
}
