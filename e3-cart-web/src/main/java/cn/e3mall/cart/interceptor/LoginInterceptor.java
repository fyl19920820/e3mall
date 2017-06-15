package cn.e3mall.cart.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import cn.e3mall.common.pojo.E3Result;
import cn.e3mall.common.utils.CookieUtils;
import cn.e3mall.pojo.TbUser;
import cn.e3mall.sso.service.UserService;

/**
 * 对请求进行拦截,判断是否登录用户,如果登录就将用户放到request中
 * @author feng
 *
 */

public class LoginInterceptor implements HandlerInterceptor {
	
	@Autowired
	private UserService userService;
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		//从cookie获取用户标识,uuid生成的
		String sessionId = CookieUtils.getCookieValue(request, "SESSIONID");
		if (StringUtils.isBlank(sessionId)) {
			return true;
		}
		//如果cookie中存在,从缓存中获取用户
		E3Result result = userService.getUserByToken(sessionId);
		//如果redis中没有对应的用户,说明已过期
		if (result.getStatus() == 400) {
			return true;
		}
		TbUser user = (TbUser) result.getData();
		//将用户放到request中
		request.setAttribute("USER", user);
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, ModelAndView arg3)
			throws Exception {
	}

	@Override
	public void afterCompletion(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, Exception arg3)
			throws Exception {
	}

}
