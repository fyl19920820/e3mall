package cn.e3mall.common.exception;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

public class GlobalExceptionReslover implements HandlerExceptionResolver {

	Logger logger = LoggerFactory.getLogger(GlobalExceptionReslover.class);
	
	@Override
	public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler,
			Exception exception) {
		//写日志
		logger.error("系统发生错误", exception);
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("message", "系统发生异常,请找管理员");
		modelAndView.setViewName("error");;
		return modelAndView;
	}


}
