package com.lc.usercenter.common;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author Lc
 * @Date 2023/6/29
 * @Description
 */
@Data
public class RequestPage implements Serializable {

    private static final long serialVersionUID = -2923774242599548680L;

    protected long pageNum;

    protected long pageSize;
}
