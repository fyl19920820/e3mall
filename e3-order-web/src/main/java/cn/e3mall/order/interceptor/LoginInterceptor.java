package cn.e3mall.order.interceptor;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import cn.e3mall.cart.service.CartService;
import cn.e3mall.common.pojo.E3Result;
import cn.e3mall.common.utils.CookieUtils;
import cn.e3mall.common.utils.JsonUtils;
import cn.e3mall.pojo.TbItem;
import cn.e3mall.pojo.TbUser;
import cn.e3mall.sso.service.UserService;

public class LoginInterceptor implements HandlerInterceptor {
	@Autowired
	private UserService userService;
	
	@Autowired
	private CartService cartService;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		//判断用户是否登录
		String sessionId = CookieUtils.getCookieValue(request, "SESSIONID");
		if (StringUtils.isBlank(sessionId)) {
			response.sendRedirect("http://localhost:8088/page/login?redirect="+request.getRequestURI());
			return false;
		}
		E3Result result = userService.getUserByToken(sessionId);
		if (result.getStatus() != 200) {
			response.sendRedirect("http://localhost:8088/page/login?redirect="+request.getRequestURI());
			return false;
		}
		TbUser user = (TbUser) result.getData();
		request.setAttribute("USER", user);
		//如果用户登录,需要将cookie中的购物车合并到redis中,合并之后将cookie删除
		String cartStr = CookieUtils.getCookieValue(request, "cart", true);
		if (StringUtils.isNotBlank(cartStr)) {
			List<TbItem> list = JsonUtils.jsonToList(cartStr, TbItem.class);
			cartService.mergeCart(user.getId(), list);
			CookieUtils.deleteCookie(request, response, "cart");
		}
		
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
