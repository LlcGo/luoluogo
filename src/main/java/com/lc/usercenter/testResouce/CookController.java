package com.lc.usercenter.testResouce;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * controller层
 */
@RestController
@RequestMapping("/cook")
public class CookController {
 
	@Resource(name = "cookPatato")
	private Cook cook;
	
	@RequestMapping("/open")
	public String open() {
		return cook.open();
	}
  
    @RequestMapping("/cooking")
	public String cooking() {
		return cook.cooking();
	}
  
    @RequestMapping("/close")
	public String close() {
		return cook.close();
	}
}
