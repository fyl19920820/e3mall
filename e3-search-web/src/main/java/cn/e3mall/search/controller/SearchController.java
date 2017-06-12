package cn.e3mall.search.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import cn.e3mall.common.pojo.SearchResult;
import cn.e3mall.search.service.SearchService;

@Controller
public class SearchController {
	@Autowired
	private SearchService searchService;

	@RequestMapping("search.html")
	public String search(
			String keyword,@RequestParam(defaultValue="1") int page,
			@RequestParam(defaultValue="60")int rows,Model model) throws Exception{
		keyword = new String(keyword.getBytes("iso8859-1"), "utf-8");
		SearchResult searchResult = searchService.search(keyword, page, rows);
		model.addAttribute("query", keyword);
		model.addAttribute("page", page);
		model.addAttribute("totalPages", searchResult.getTotalPages());
		model.addAttribute("recourdCount", searchResult.getRecourdCount());
		model.addAttribute("itemList", searchResult.getItemList());
		
		return "search";
	}
	
	/**
	 * 测试一下全局异常处理器
	 * @return
	 */
	@RequestMapping("error")
	public String exceptionTest(){
		int i = 1/0;
		return "search";
	}

}
