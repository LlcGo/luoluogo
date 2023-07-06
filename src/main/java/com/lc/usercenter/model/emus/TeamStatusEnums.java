package com.lc.usercenter.model.emus;

import com.lc.usercenter.common.ErrorCode;
import com.lc.usercenter.exception.BusinessException;

public enum TeamStatusEnums {


    PUBLICE(0,"公开"),
    PRIVER(1,"私有"),
    ENCIPHER(2,"加密");

    private String value;
    private int status;

    TeamStatusEnums( Integer status,String value) {
        this.value = value;
        this.status = status;
    }


    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public static TeamStatusEnums getEnumBystatus(Integer status){
        if(status == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"参数不正常");
        }
        TeamStatusEnums[] values = TeamStatusEnums.values();
        for (TeamStatusEnums statusEnums : values) {
            if(statusEnums.getStatus() == status){
                return statusEnums;
            }
        }
        return null;
    }


}
