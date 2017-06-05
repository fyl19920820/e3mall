package cn.e3mall.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.e3mall.common.pojo.E3Result;
import cn.e3mall.common.pojo.EasyUITreeNode;
import cn.e3mall.content.service.ContentCategoryService;
import cn.e3mall.pojo.TbContentCategory;

@Controller
public class ContentCategoryController {

	@Autowired
	private ContentCategoryService contentCategoryService;
	
	@RequestMapping("/content/category/list")
	@ResponseBody
	public List<EasyUITreeNode> getContentCategoryListByParentId(
			@RequestParam(name="id",defaultValue="0") Long parentId){
		List<EasyUITreeNode> list = contentCategoryService.getContentCategoryListByParentId(parentId);
		return list;
	}
	
	@RequestMapping("/content/category/create")
	@ResponseBody
	public E3Result addContentCategory(Long parentId,String name){
		TbContentCategory contentCategory = contentCategoryService.addContentCategory(parentId, name);
		return E3Result.ok(contentCategory);
	}
	
	@RequestMapping("/content/category/update")
	@ResponseBody
	public E3Result updateContentCategory(Long id,String name){
		contentCategoryService.updateContentCategory(id, name);
		return null;
	}
	
	@RequestMapping("/content/category/delete")
	@ResponseBody
	public E3Result deleteContentCategory(Long id){
		contentCategoryService.deleteBeforeInit(id);
		contentCategoryService.deleteContentCategory(id);
		return null;
	}
}
