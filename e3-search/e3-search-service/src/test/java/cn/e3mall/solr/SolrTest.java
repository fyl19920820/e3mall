package cn.e3mall.solr;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.junit.Test;
import org.springframework.context.annotation.EnableLoadTimeWeaving;

public class SolrTest {

	@Test
	public void addDocument() throws Exception{
		SolrServer server = new HttpSolrServer("http://192.168.119.133:8080/solr/collection1");
		SolrInputDocument  document = new SolrInputDocument();
		document.addField("id", "test2");
		document.addField("item_title", "手机2");
		document.addField("item_sell_point", "b");
		server.add(document);
		server.commit();
	}
	
	@Test
	public void delDocument() throws Exception{
		SolrServer server = new HttpSolrServer("http://192.168.119.133:8080/solr/collection1");
		server.deleteByQuery("*:*");
		server.commit();
	}
	
	@Test
	public void selectDocument()throws Exception {
		SolrServer server = new HttpSolrServer("http://192.168.119.133:8080/solr/collection1");
		SolrQuery query = new SolrQuery();
		query.setQuery("*:*");
		QueryResponse queryResponse = server.query(query);
		SolrDocumentList results = queryResponse.getResults();
		System.out.println("查询总数为:"+results.getNumFound());
		for (SolrDocument solrDocument : results) {
			System.out.println(solrDocument.get("id"));
			System.out.println(solrDocument.get("item_title"));
			System.out.println(solrDocument.get("item_sell_point"));
		}
	}
	
	@Test
	public void selectDocumentWithHighLighting() throws Exception{
		SolrServer server = new HttpSolrServer("http://192.168.119.133:8080/solr/collection1");
		SolrQuery query = new SolrQuery();
		query.setQuery("手机");
		query.setStart(0);
		query.setRows(10);
		query.set("df", "item_title");
		query.setHighlight(true);
		query.addHighlightField("item_title");
		query.setHighlightSimplePre("<em>");
		query.setHighlightSimplePost("</em>");
		QueryResponse queryResponse = server.query(query);
		SolrDocumentList results = queryResponse.getResults();
		Map<String, Map<String, List<String>>> highlighting = queryResponse.getHighlighting();
		System.out.println("查询总数为:"+results.getNumFound());
		for (SolrDocument solrDocument : results) {
			System.out.println(solrDocument.get("id"));
			String title = "";
			List<String> list = highlighting.get(solrDocument.get("id")).get("item_title");
			if (list != null && list.size() > 0) {
				title = list.get(0);
			}else {
				title = (String) solrDocument.get("item_title");
			}
			System.out.println(title);
			System.out.println(solrDocument.get("item_sell_point"));
		}
	}
}
