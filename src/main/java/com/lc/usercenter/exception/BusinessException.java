package com.lc.usercenter.exception;

import com.lc.usercenter.common.ErrorCode;

/**
 * @Author Lc
 * @Date 2023/6/11
 * @Description
 */
public class BusinessException extends RuntimeException {
    private final Integer code;
    private final String description;

    public BusinessException(String message, Integer code, String description) {
        super(message);
        this.code = code;
        this.description = description;
    }

    public BusinessException(ErrorCode errorCode, String description) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.description = description;
    }

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.description = null;
    }

    public Integer getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
