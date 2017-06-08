package cn.e3mall.search.dao;

import org.apache.solr.client.solrj.SolrQuery;

import cn.e3mall.common.pojo.SearchResult;

public interface SearchDao {
	
	SearchResult search(SolrQuery query)throws Exception;
}
