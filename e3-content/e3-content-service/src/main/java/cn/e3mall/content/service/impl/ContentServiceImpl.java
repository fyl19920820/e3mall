package cn.e3mall.content.service.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import cn.e3mall.common.jedis.JedisClient;
import cn.e3mall.common.pojo.EasyUIDataGaridResult;
import cn.e3mall.common.utils.JsonUtils;
import cn.e3mall.content.service.ContentService;
import cn.e3mall.mapper.TbContentMapper;
import cn.e3mall.pojo.TbContent;
import cn.e3mall.pojo.TbContentExample;
import cn.e3mall.pojo.TbContentExample.Criteria;

@Service
public class ContentServiceImpl implements ContentService {

	@Autowired
	private TbContentMapper contentMapper;
	@Autowired
	private JedisClient jedisClient;

	@Override
	public EasyUIDataGaridResult getContentListByCategoryId(long categoryId,int page,int rows) {
		//调用pagehelper插件
		PageHelper.startPage(page, rows);
		TbContentExample example = new TbContentExample();
		Criteria criteria = example.createCriteria();
		criteria.andCategoryIdEqualTo(categoryId);
		List<TbContent> list = contentMapper.selectByExample(example);
		//转换成pageinfo
		PageInfo<TbContent> pageInfo = new PageInfo<>(list);
		long total = pageInfo.getTotal();
		EasyUIDataGaridResult result = new EasyUIDataGaridResult();
		result.setTotal(total);
		result.setRows(list);
		return result;
	}

	@Override
	public void addContent(TbContent content) {
		Date date = new Date();
		content.setCreated(date);
		content.setUpdated(date);
		contentMapper.insert(content);
		//清除缓存
		jedisClient.hdel("CONTENT", content.getCategoryId()+"");
	}

	@Override
	public void updateContent(TbContent content) {
		content.setUpdated(new Date());
		contentMapper.updateByPrimaryKeySelective(content);
		//清除缓存
		jedisClient.hdel("CONTENT", content.getCategoryId()+"");
	}

	@Override
	public void deleteContent(String ids) {
		String[] strings = ids.split(",");
		for (String string : strings) {
			int id =  Integer.valueOf(string);
			//清除缓存
			TbContent content = contentMapper.selectByPrimaryKey((long)id);
			jedisClient.hdel("CONTENT", content.getCategoryId()+"");
			contentMapper.deleteByPrimaryKey((long)id);
		}
	}

	@Override
	public List<TbContent> getContentListByCategoryId(long categoryId) {
		try {
			String string = jedisClient.hget("CONTENT", categoryId+"");
			if (StringUtils.isNotBlank(string)) {
				List<TbContent> list = JsonUtils.jsonToList(string, TbContent.class);
				return list;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		TbContentExample example = new TbContentExample();
		Criteria criteria = example.createCriteria();
		criteria.andCategoryIdEqualTo(categoryId);
		List<TbContent> list = contentMapper.selectByExample(example);
		try {
			jedisClient.hset("CONTENT", categoryId+"", JsonUtils.objectToJson(list));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

}
