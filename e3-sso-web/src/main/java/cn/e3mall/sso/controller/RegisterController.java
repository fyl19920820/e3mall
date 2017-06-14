package cn.e3mall.sso.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.e3mall.common.pojo.E3Result;
import cn.e3mall.pojo.TbUser;
import cn.e3mall.sso.service.UserService;

@Controller
public class RegisterController {
	
	@Autowired
	private UserService userService;

	@RequestMapping("page/register")
	public String showRegister(){
		return "register";
	}
	
	@RequestMapping("/user/check/{data}/{type}")
	@ResponseBody
	public E3Result checkUser(@PathVariable String data,@PathVariable Integer type){
		E3Result result = userService.checkUser(data, type);
		return result;
	}
	
	@RequestMapping("user/register")
	@ResponseBody
	public E3Result register(TbUser user){
		E3Result result = userService.register(user);
		return result;
	}
	
	
}
