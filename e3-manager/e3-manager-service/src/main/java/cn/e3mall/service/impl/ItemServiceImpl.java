package cn.e3mall.service.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.command.ActiveMQTopic;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import cn.e3mall.common.jedis.JedisClient;
import cn.e3mall.common.pojo.E3Result;
import cn.e3mall.common.pojo.EasyUIDataGaridResult;
import cn.e3mall.common.utils.IDUtils;
import cn.e3mall.common.utils.JsonUtils;
import cn.e3mall.mapper.TbItemDescMapper;
import cn.e3mall.mapper.TbItemMapper;
import cn.e3mall.pojo.TbItem;
import cn.e3mall.pojo.TbItemDesc;
import cn.e3mall.pojo.TbItemExample;
import cn.e3mall.service.ItemService;
@Service
public class ItemServiceImpl implements ItemService {

	@Autowired
	private TbItemMapper itemMapper;
	
	@Autowired
	private TbItemDescMapper itemDescMapper;
	
	@Autowired
	private JmsTemplate jmsTemplate;
	@Resource(name="addItemTopic")
	private ActiveMQTopic addItemTopic;
	
	@Autowired
	private JedisClient jedisClient;
	
	@Value("${ITEM_INFO_EXPIRE}")
	private Integer ITEM_INFO_EXPIRE;

	@Override
	public TbItem getItemById(long id) {
		try {
			String itemJson =  jedisClient.get("ITEM_INFO:"+id+":BASE");
			if (StringUtils.isNotBlank(itemJson)) {
				TbItem tbItem = JsonUtils.jsonToPojo(itemJson, TbItem.class);
				return tbItem;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		TbItem item = itemMapper.selectByPrimaryKey(id);
		try {
			jedisClient.set("ITEM_INFO:"+id+":BASE", JsonUtils.objectToJson(item));
			jedisClient.expire("ITEM_INFO:"+id+":BASE", ITEM_INFO_EXPIRE);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return item;

	}

	@Override
	public EasyUIDataGaridResult getItemList(int page, int rows) {
		EasyUIDataGaridResult result = new EasyUIDataGaridResult();
		PageHelper.startPage(page, rows);
		TbItemExample example = new TbItemExample();
		List<TbItem> list = itemMapper.selectByExample(example);
		PageInfo<TbItem> pageInfo = new PageInfo<>(list);
		result.setTotal(pageInfo.getTotal());
		result.setRows(pageInfo.getList());
		return result;
	}

	@Override
	public void addItem(TbItem item, String desc) {
		//补充item中缺少的信息
		//id值采用工具类生成,原理为当前的毫秒值后面加两位随机数
		final long id = IDUtils.genItemId();
		item.setId(id);
		Date date = new Date();
		//商品状态，1-正常，2-下架，3-删除
		item.setStatus((byte) 1);
		item.setCreated(date);
		item.setUpdated(date);
		itemMapper.insert(item);
		
		TbItemDesc itemDesc = new TbItemDesc();
		itemDesc.setItemId(id);
		itemDesc.setItemDesc(desc);
		itemDesc.setCreated(date);
		itemDesc.setUpdated(date);
		itemDescMapper.insert(itemDesc);
		jmsTemplate.send(addItemTopic, new MessageCreator() {
			
			@Override
			public Message createMessage(Session session) throws JMSException {
				TextMessage textMessage = session.createTextMessage(id+"");
				return textMessage;
			}
		});
		
	}

	@Override
	public TbItemDesc queryItemDescById(long id) {
		try {
			String itemDescJson =  jedisClient.get("ITEM_INFO:"+id+":DESC");
			if (StringUtils.isNotBlank(itemDescJson)) {
				TbItemDesc tbItemDesc = JsonUtils.jsonToPojo(itemDescJson, TbItemDesc.class);
				return tbItemDesc;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		TbItemDesc itemDesc = itemDescMapper.selectByPrimaryKey(id);
		try {
			jedisClient.set("ITEM_INFO:"+id+":DESC", JsonUtils.objectToJson(itemDesc));
			jedisClient.expire("ITEM_INFO:"+id+":DESC", ITEM_INFO_EXPIRE);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return itemDesc;
	}

	@Override
	public void updateItem(TbItem item) {
		itemMapper.updateByPrimaryKey(item);
	}

	@Override
	public void deleteItem(String ids) {
		String[] itemIds = ids.split(",");
		for (String itemId : itemIds) {
			int id = Integer.valueOf(itemId);
			TbItem item = itemMapper.selectByPrimaryKey((long)id);
			item.setStatus((byte)3);
			itemMapper.updateByPrimaryKey(item);
		}
	}

	/**
	 * 下架商品
	 */
	@Override
	public void instockItem(String ids) {
		String[] itemIds = ids.split(",");
		for (String itemId : itemIds) {
			int id = Integer.valueOf(itemId);
			TbItem item = itemMapper.selectByPrimaryKey((long)id);
			item.setStatus((byte)2);
			itemMapper.updateByPrimaryKey(item);
		}
	}

	/**
	 * 上架商品
	 */
	@Override
	public void reshelfItem(String ids) {
		String[] itemIds = ids.split(",");
		for (String itemId : itemIds) {
			int id = Integer.valueOf(itemId);
			TbItem item = itemMapper.selectByPrimaryKey((long)id);
			item.setStatus((byte)1);
			itemMapper.updateByPrimaryKey(item);
		}
	}

}
