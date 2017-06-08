package cn.e3mall.search.dao.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import cn.e3mall.common.pojo.SearchItem;
import cn.e3mall.common.pojo.SearchResult;
import cn.e3mall.search.dao.SearchDao;

@Repository
public class SearchDaoImpl implements SearchDao {
	
	@Autowired
	private SolrServer solrServer;

	@Override
	public SearchResult search(SolrQuery query) throws Exception {
		QueryResponse queryResponse = solrServer.query(query);
		SolrDocumentList solrDocumentList = queryResponse.getResults();
		Map<String, Map<String, List<String>>> highlighting = queryResponse.getHighlighting();
		List<SearchItem> itemList = new ArrayList<>();
		for (SolrDocument solrDocument : solrDocumentList) {
			SearchItem item = new SearchItem();
			String id = (String) solrDocument.get("id");
			String title = null;
			List<String> highlightingList = highlighting.get(solrDocument.get("id")).get("item_title");
			if (highlightingList != null && highlightingList.size() > 0) {
				title = highlightingList.get(0);
			}else {
				title = (String) solrDocument.get("item_title");
			}
			String sell_point = (String) solrDocument.get("item_sell_point");
			long price =  (long) solrDocument.get("item_price");
			String image =  (String) solrDocument.get("item_image");
			String category_name = (String) solrDocument.get("item_category_name");
			item.setId(id);
			item.setTitle(title);
			item.setSell_point(sell_point);
			item.setPrice(price);
			item.setImage(image);
			item.setCategory_name(category_name);
			itemList.add(item);
		}
		long recourdCount = solrDocumentList.getNumFound();
		SearchResult searchResult = new SearchResult();
		searchResult.setItemList(itemList);
		searchResult.setRecourdCount(recourdCount);
		return searchResult;
	}


}
