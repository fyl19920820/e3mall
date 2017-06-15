package cn.e3mall.cart.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.e3mall.cart.service.CartService;
import cn.e3mall.common.jedis.JedisClient;
import cn.e3mall.common.pojo.E3Result;
import cn.e3mall.common.utils.JsonUtils;
import cn.e3mall.mapper.TbItemMapper;
import cn.e3mall.pojo.TbItem;

@Service
public class CartServiceImpl implements CartService {
	
	@Autowired
	private JedisClient jedisClient;
	
	@Autowired
	private TbItemMapper itemMapper;

	@Override
	public E3Result addItemToCart(long userId, long itemId, int num) {
		//判断商品是否存在购物车中
		Boolean flag = jedisClient.hexists("CART:"+userId, itemId+"");
		//如果存在
		if (flag) {
			String itemStr = jedisClient.hget("CART:"+userId, itemId+"");
			TbItem item = JsonUtils.jsonToPojo(itemStr, TbItem.class);
			item.setNum(item.getNum() + num);
			jedisClient.hset("CART:"+userId, itemId+"", JsonUtils.objectToJson(item));
			return E3Result.ok();
		}
		//如果不存在,需要查找数据库
		TbItem item = itemMapper.selectByPrimaryKey(itemId);
		//补全属性
		item.setNum(num);
		String image = item.getImage();
		if (StringUtils.isNotBlank(image)) {
			item.setImage(image.split(",")[0]);
		}
		jedisClient.hset("CART:"+userId, itemId+"", JsonUtils.objectToJson(item));
		return E3Result.ok();
	}

	/**
	 * 合并购物车,将cookie中的数据合并到redis中
	 */
	@Override
	public E3Result mergeCart(long userId, List<TbItem> list) {
		for (TbItem tbItem : list) {
			addItemToCart(userId, tbItem.getId(), tbItem.getNum());
		}
		return E3Result.ok();
	}

	@Override
	public List<TbItem> getCart(long userId) {
		List<String> listStr = jedisClient.hvals("CART:"+userId);
		List<TbItem> list = new ArrayList<>();
		for (String string : listStr) {
			TbItem item = JsonUtils.jsonToPojo(string, TbItem.class);
			list.add(item);
		}
		return list;
	}

	@Override
	public E3Result updateItemFromCart(long userId, long itemId, int num) {
		String itemStr = jedisClient.hget("CART:"+userId, itemId+"");
		TbItem item = JsonUtils.jsonToPojo(itemStr, TbItem.class);
		item.setNum(num);
		jedisClient.hset("CART:"+userId, itemId+"", JsonUtils.objectToJson(item));
		return E3Result.ok();
	}

	@Override
	public E3Result deleteItemFromCart(long userId, long itemId) {
		jedisClient.hdel("CART:"+userId, itemId+"");
		return E3Result.ok();
	}

}
