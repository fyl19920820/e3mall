package cn.e3mall.content.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import cn.e3mall.content.service.ContentService;
import cn.e3mall.pojo.TbContent;

@Controller
public class IndexController {
	
	@Value("${index.slider.cid}")
	private long categoryId;

	@Autowired
	private ContentService contentService;
	@RequestMapping("/index")
	public String showIndex(Model model){
		List<TbContent> ad1List = contentService.getContentListByCategoryId(categoryId);
		model.addAttribute("ad1List", ad1List);
		return "index";
	}
}
