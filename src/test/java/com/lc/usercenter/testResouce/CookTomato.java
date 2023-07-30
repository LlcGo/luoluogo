package com.lc.usercenter.testResouce;

import org.springframework.stereotype.Service;

/**
 * service接口 实现类
 * 炒西红柿
 */
@Service
public class CookTomato implements Cook {

	public String open() {
		return "炒西红柿前打开油烟机并开火";
	}

  public String cooking() {
		return "炒西红柿中~";
	}

  public String close() {
		return "炒西红柿后关闭油烟机并关火";
	}
}
