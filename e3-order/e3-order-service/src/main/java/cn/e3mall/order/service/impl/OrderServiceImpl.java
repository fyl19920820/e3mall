package cn.e3mall.order.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import cn.e3mall.common.jedis.JedisClient;
import cn.e3mall.common.pojo.E3Result;
import cn.e3mall.mapper.TbOrderItemMapper;
import cn.e3mall.mapper.TbOrderMapper;
import cn.e3mall.mapper.TbOrderShippingMapper;
import cn.e3mall.order.pojo.OrderInfo;
import cn.e3mall.order.service.OrderService;
import cn.e3mall.pojo.TbOrderItem;
import cn.e3mall.pojo.TbOrderShipping;

/**
 * 订单处理Service
 * <p>Title: OrderServiceImpl</p>
 * <p>Description: </p>
 * <p>Company: www.itcast.cn</p> 
 * @version 1.0
 */
@Service
public class OrderServiceImpl implements OrderService {

	@Autowired
	private TbOrderMapper orderMapper;
	@Autowired
	private TbOrderItemMapper orderItemMapper;
	@Autowired
	private TbOrderShippingMapper orderShippingMapper;
	@Autowired
	private JedisClient jedisClient;
	@Value("${order.id.genkey}")
	private String orderIdGenKey;
	@Value("${order.id.start}")
	private String orderIdStart;
	@Value("${order.item.id.genkey}")
	private String orderItemIdGenKey;
	
	@Override
	public E3Result createOrder(OrderInfo orderInfo) {
		// 1、生成订单号
		if (!jedisClient.exists(orderIdGenKey)) {
			jedisClient.set(orderIdGenKey, orderIdStart);
		}
		String orderId = jedisClient.incr(orderIdGenKey).toString();
		// 2、补全pojo属性
		orderInfo.setOrderId(orderId);
		//状态：1、未付款，2、已付款，3、未发货，4、已发货，5、交易成功，6、交易关闭
		orderInfo.setStatus(1);
		orderInfo.setCreateTime(new Date());
		orderInfo.setUpdateTime(new Date());
		// 3、向Tb_order表插入数据
		orderMapper.insert(orderInfo);
		// 4、取商品列表
		List<TbOrderItem> orderItems = orderInfo.getOrderItems();
		// 5、循环商品列表向订单明细表插入数据
		for (TbOrderItem tbOrderItem : orderItems) {
			//生成id
			Long oid = jedisClient.incr(orderItemIdGenKey);
			tbOrderItem.setId(oid.toString());
			tbOrderItem.setOrderId(orderId);
			//插入数据
			orderItemMapper.insert(tbOrderItem);
		}
		// 6、取物流信息
		TbOrderShipping orderShipping = orderInfo.getOrderShipping();
		// 7、补全属性
		orderShipping.setOrderId(orderId);
		orderShipping.setCreated(new Date());
		orderShipping.setUpdated(new Date());
		// 8、向物流表插入数据
		orderShippingMapper.insert(orderShipping);
		// 9、返回订单号。
		return E3Result.ok(orderId);
	}

}
