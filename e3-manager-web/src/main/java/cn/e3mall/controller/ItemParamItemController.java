package cn.e3mall.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.e3mall.pojo.TbItemParamItem;
import cn.e3mall.service.ItemParamItemService;

@Controller
public class ItemParamItemController {
	
	@Autowired
	private ItemParamItemService itemParamItemService;
	
	@RequestMapping("/rest/item/param/item/query/{itemId}")
	@ResponseBody
	public TbItemParamItem queryItemParamItemByItemId(@PathVariable Long itemId) {
		TbItemParamItem itemParamItem = itemParamItemService.queryItemParamItemByItemId(itemId);
		return itemParamItem;
	}
}
