package cn.e3mall.search.service.impl;

import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.e3mall.common.pojo.SearchItem;
import cn.e3mall.common.pojo.SearchResult;
import cn.e3mall.search.dao.SearchDao;
import cn.e3mall.search.service.SearchService;

@Service
public class SearchServiceImpl implements SearchService {

	@Autowired
	private SearchDao searchDao;
	
	@Override
	public SearchResult search(String keyword, int page, int rows) throws Exception {
		SolrQuery query = new SolrQuery();
		query.set("q", keyword);
		query.setStart((page -1)*rows);
		query.setRows(rows);
		query.set("df", "item_keywords");
		query.setHighlight(true);
		query.addHighlightField("item_title");
		query.setHighlightSimplePre("<em style = 'color:red'>");
		query.setHighlightSimplePost("</em>");
		SearchResult searchResult = searchDao.search(query);
		List<SearchItem> itemList = searchResult.getItemList();
		long recourdCount = searchResult.getRecourdCount();
		long totalPages = recourdCount / rows;
		if (recourdCount % rows != 0) {
			totalPages ++;
		}
		searchResult.setTotalPages(totalPages);
		return searchResult;
	}

}
