package cn.e3mall.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.e3mall.common.pojo.E3Result;
import cn.e3mall.common.pojo.EasyUIDataGaridResult;
import cn.e3mall.pojo.TbItem;
import cn.e3mall.pojo.TbItemDesc;
import cn.e3mall.service.ItemService;

@Controller
public class ItemController {

	@Autowired
	private ItemService itemService;
	
	@RequestMapping("/item/{itemId}")
	@ResponseBody
	public TbItem getItemById(@PathVariable Long itemId) {
		TbItem tbItem = itemService.getItemById(itemId);
		return tbItem;
	}
	
	@RequestMapping("/item/list")
	@ResponseBody
	public EasyUIDataGaridResult getItemList(Integer page,Integer rows) {
		EasyUIDataGaridResult result = itemService.getItemList(page, rows);
		return result;
	}
	
	@RequestMapping("/item/save")
	@ResponseBody
	public E3Result addItem(TbItem item,String desc){
		itemService.addItem(item, desc);
		return E3Result.ok();
	}
	/**
	 * 将编辑页面返回给window窗口
	 * @return
	 */
	@RequestMapping("/rest/page/item-edit")
	public String editItem(){
		return "item-edit";
	}
	
	@RequestMapping("/rest/item/query/item/desc/{itemId}")
	@ResponseBody
	public E3Result queryItemDescById(@PathVariable("itemId") Long id){
		TbItemDesc itemDesc = itemService.queryItemDescById(id);
		return E3Result.build(200, "", itemDesc);
	}
	
	@RequestMapping("/rest/item/update")
	@ResponseBody
	public E3Result updateItem(TbItem item){
		TbItem temp =  itemService.getItemById(item.getId());
		item.setStatus(temp.getStatus());
		item.setCreated(temp.getCreated());
		item.setUpdated(temp.getUpdated());
		itemService.updateItem(item);
		return E3Result.ok();
	}
	
	@RequestMapping("/rest/item/delete")
	@ResponseBody
	public E3Result deleteItem(String ids){
		itemService.deleteItem(ids);
		return E3Result.ok();
	}
	
	@RequestMapping("/rest/item/instock")
	@ResponseBody
	public E3Result instockItem(String ids){
		itemService.instockItem(ids);
		return E3Result.ok();
	}
	
	@RequestMapping("/rest/item/reshelf")
	@ResponseBody
	public E3Result reshelfItem(String ids){
		itemService.reshelfItem(ids);
		return E3Result.ok();
	}
	
}
