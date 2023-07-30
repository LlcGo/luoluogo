package com.lc.usercenter.testResouce;

import org.springframework.stereotype.Service;

/**
 * service接口 实现类
 * 炒土豆
 */
@Service
public class CookPatato implements Cook {

	public String open() {
		return "炒土豆丝前打开油烟机并开火";
	}

    public String cooking() {
		return "炒土豆丝中~";
	}

    public String close() {
		return "炒土豆丝后关闭油烟机并关火";
	}
}
