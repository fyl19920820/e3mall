package cn.e3mall.cart.service;

import java.util.List;

import cn.e3mall.common.pojo.E3Result;
import cn.e3mall.pojo.TbItem;

public interface CartService {
	E3Result addItemToCart(long userId,long itemId,int num);
	E3Result mergeCart(long userId,List<TbItem> list);
	List<TbItem> getCart(long userId);
	E3Result updateItemFromCart(long userId,long itemId,int num);
	E3Result deleteItemFromCart(long userId,long itemId);
}
