package cn.e3mall.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.e3mall.common.pojo.EasyUITreeNode;
import cn.e3mall.pojo.TbItemCat;
import cn.e3mall.service.ItemCatService;

@Controller
public class ItemCatController {

	@Autowired
	private ItemCatService itemCatService;
	/**
	 * 根据父节点ID获取子节点集合
	 */
	@RequestMapping("/item/cat/list")
	@ResponseBody
	public List<EasyUITreeNode> getItemCatListByParentId(@RequestParam(value="id",defaultValue="0")Long parentId){
		List<EasyUITreeNode> list = itemCatService.getItemCatListByParentId(parentId);
		return list;
	}
	
	@RequestMapping("/item/cat/{id}")
	@ResponseBody
	public TbItemCat getItemCatById(@PathVariable Long id){
		return itemCatService.getItemCatById(id);
	}
}
