package cn.e3mall.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.e3mall.common.pojo.E3Result;
import cn.e3mall.common.pojo.EasyUIDataGaridResult;
import cn.e3mall.content.service.ContentService;
import cn.e3mall.pojo.TbContent;

@Controller
public class ContentController {

	@Autowired
	private ContentService contentService;
	
	@RequestMapping("/content/query/list")
	@ResponseBody
	public EasyUIDataGaridResult getContentListByCategoryId(long categoryId,int page,int rows) {
		EasyUIDataGaridResult result = contentService.getContentListByCategoryId(categoryId, page, rows);
		return result;
	}
	
	@RequestMapping("/content/save")
	@ResponseBody
	public E3Result addContent(TbContent content) {
		contentService.addContent(content);
		return E3Result.ok();
	}
	
	@RequestMapping("/rest/content/edit")
	@ResponseBody
	public E3Result updateContent(TbContent content){
		contentService.updateContent(content);
		return E3Result.ok();
	}
	
	@RequestMapping("/content/delete")
	@ResponseBody
	public E3Result deleteContent(String ids){
		contentService.deleteContent(ids);
		return E3Result.ok();
	}
}
