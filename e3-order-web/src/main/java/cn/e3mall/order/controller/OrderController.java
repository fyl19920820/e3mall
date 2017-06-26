package cn.e3mall.order.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import cn.e3mall.cart.service.CartService;
import cn.e3mall.common.pojo.E3Result;
import cn.e3mall.order.pojo.OrderInfo;
import cn.e3mall.order.service.OrderService;
import cn.e3mall.pojo.TbItem;
import cn.e3mall.pojo.TbUser;

@Controller
public class OrderController {
	@Autowired
	private CartService cartService;
	
	@Autowired
	private OrderService orderService;

	@RequestMapping("/order/order-cart")
	public String showOrderCart(HttpServletRequest request){
		TbUser user = (TbUser) request.getAttribute("USER");
		long userId = user.getId();
		List<TbItem> list = cartService.getCart(userId);
		request.setAttribute("cartList", list);
		return "order-cart";
	}
	
	@RequestMapping(value="/order/create", method=RequestMethod.POST)
	public String createOrder(HttpServletRequest request, OrderInfo orderInfo) {
		//取用户信息
		TbUser user = (TbUser) request.getAttribute("USER");
		//设置用户信息
		orderInfo.setUserId(user.getId());
		orderInfo.setBuyerNick(user.getUsername());
		//生成订单
		E3Result e3Result = orderService.createOrder(orderInfo);
		request.setAttribute("orderId", e3Result.getData());
		request.setAttribute("payment", orderInfo.getPayment());
		//清空购物车数据
		cartService.clearCart(user.getId());
		//返回逻辑视图
		return "success";
	}
}
