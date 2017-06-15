package cn.e3mall.cart.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.e3mall.cart.service.CartService;
import cn.e3mall.common.pojo.E3Result;
import cn.e3mall.common.utils.CookieUtils;
import cn.e3mall.common.utils.JsonUtils;
import cn.e3mall.pojo.TbItem;
import cn.e3mall.pojo.TbUser;
import cn.e3mall.service.ItemService;

@Controller
public class CartController {
	
	@Autowired
	private ItemService itemService;
	@Value("${COOKIE_CART_EXPIRE}")
	private Integer COOKIE_CART_EXPIRE;
	
	@Autowired
	private CartService cartService;

	@RequestMapping("cart/add/{itemId}")
	public String addItemToCart(@PathVariable Long itemId,Integer num,
			HttpServletRequest request,HttpServletResponse response){
		//判断用户是否登录
		TbUser user = (TbUser) request.getAttribute("USER");
		if (user != null) {
			cartService.addItemToCart(user.getId(), itemId, num);
			return "cartSuccess";
		}
		//从cookie中获得购物车的json字符串
		List<TbItem> cart = getCartFromCookie(request);
		//判断要添加的商品是否在购物车中
		boolean flag = false;
		for (TbItem tbItem : cart) {
			if (tbItem.getId() == itemId.longValue()) {
				tbItem.setNum(tbItem.getNum() + num);
				flag = true;
				break;
			}
		}
		//如果购物车中不存在
		if (!flag) {
			//从数据库中查找
			TbItem item = itemService.getItemById(itemId);
			//取一张图片
			String image = item.getImage();
			item.setImage(image.split(",")[0]);
			//设置数量
			item.setNum(num);
			cart.add(item);
		}
		//将购物车写入cookie中
		CookieUtils.setCookie(request, response, "cart", JsonUtils.objectToJson(cart), COOKIE_CART_EXPIRE, true);
		return "cartSuccess";
	}
	//获取cookie中的购物车
	private List<TbItem> getCartFromCookie(HttpServletRequest request){
		String cartJson = CookieUtils.getCookieValue(request, "cart", true);
		if (StringUtils.isBlank(cartJson)) {
			return new ArrayList<>();
		}
		List<TbItem> list = JsonUtils.jsonToList(cartJson, TbItem.class);
		return list;
	}
	
	@RequestMapping("/cart/cart")
	public String showCart(HttpServletRequest request,HttpServletResponse response){
		//判断用户是否登录
		TbUser user = (TbUser) request.getAttribute("USER");
		if (user != null) {
			List<TbItem> list = getCartFromCookie(request);
			cartService.mergeCart(user.getId(), list);
			//删除cookie
			CookieUtils.deleteCookie(request, response, "cart");
			list = cartService.getCart(user.getId());
			request.setAttribute("cartList", list);
			return "cart";
		}
		List<TbItem> list = getCartFromCookie(request);
		request.setAttribute("cartList", list);
		return "cart";
	}
	
	@RequestMapping("/cart/update/num/{itemId}/{num}")
	@ResponseBody
	public E3Result updateNum(@PathVariable Long itemId,@PathVariable Integer num,
			HttpServletRequest request,HttpServletResponse response){
		//判断用户是否登录
		TbUser user = (TbUser) request.getAttribute("USER");
		if (user != null) {
			E3Result result = cartService.updateItemFromCart(user.getId(), itemId, num);
			return result;
		}
		List<TbItem> list = getCartFromCookie(request);
		for (TbItem tbItem : list) {
			if (tbItem.getId() == itemId.longValue()) {
				tbItem.setNum(num);
				break;
			}
		}
		CookieUtils.setCookie(request, response, "cart", JsonUtils.objectToJson(list), COOKIE_CART_EXPIRE, true);
		return E3Result.ok();
	}
	
	@RequestMapping("/cart/delete/{itemId}")
	public String delItemFromCart(@PathVariable Long itemId,
			HttpServletRequest request,HttpServletResponse response){
		//判断用户是否登录
		TbUser user = (TbUser) request.getAttribute("USER");
		if (user != null) {
			cartService.deleteItemFromCart(user.getId(), itemId);
			return "redirect:/cart/cart.html";
		}
		List<TbItem> list = getCartFromCookie(request);
		for (TbItem tbItem : list) {
			if (tbItem.getId() == itemId.longValue()) {
				list.remove(tbItem);
				break;
			}
		}
		CookieUtils.setCookie(request, response, "cart", JsonUtils.objectToJson(list), this.COOKIE_CART_EXPIRE, true);
		return "redirect:/cart/cart.html";
	}

}
