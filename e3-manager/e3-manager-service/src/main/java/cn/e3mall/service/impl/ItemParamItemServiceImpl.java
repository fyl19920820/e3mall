package cn.e3mall.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.e3mall.mapper.TbItemParamItemMapper;
import cn.e3mall.pojo.TbItemParamItem;
import cn.e3mall.pojo.TbItemParamItemExample;
import cn.e3mall.pojo.TbItemParamItemExample.Criteria;
import cn.e3mall.service.ItemParamItemService;

@Service
public class ItemParamItemServiceImpl implements ItemParamItemService {

	@Autowired
	private TbItemParamItemMapper itemParamItemMapper;

	@Override
	public TbItemParamItem queryItemParamItemByItemId(long itemId) {
		TbItemParamItemExample itemParamItemExample = new TbItemParamItemExample();
		Criteria criteria = itemParamItemExample.createCriteria();
		criteria.andItemIdEqualTo(itemId);
		List<TbItemParamItem> list =  itemParamItemMapper.selectByExample(itemParamItemExample);
		if (list != null && list.size() > 0) {
			return list.get(0);
		}else {
			return null;
		}
	}

}
