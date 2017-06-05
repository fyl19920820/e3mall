package cn.e3mall.content.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.e3mall.common.pojo.EasyUITreeNode;
import cn.e3mall.content.service.ContentCategoryService;
import cn.e3mall.mapper.TbContentCategoryMapper;
import cn.e3mall.pojo.TbContentCategory;
import cn.e3mall.pojo.TbContentCategoryExample;
import cn.e3mall.pojo.TbContentCategoryExample.Criteria;

@Service
public class ContentCategoryServiceImpl implements ContentCategoryService {

	@Autowired
	private TbContentCategoryMapper contentCategoryMapper;
	@Override
	public List<EasyUITreeNode> getContentCategoryListByParentId(long parentId) {
		TbContentCategoryExample example = new TbContentCategoryExample();
		Criteria criteria = example.createCriteria();
		criteria.andParentIdEqualTo(parentId);
		List<TbContentCategory> list = contentCategoryMapper.selectByExample(example);
		List<EasyUITreeNode> nodeList = new ArrayList<>();
		for (TbContentCategory contentCategory : list) {
			EasyUITreeNode node = new EasyUITreeNode();
			node.setId(contentCategory.getId());
			node.setText(contentCategory.getName());
			node.setState(contentCategory.getIsParent()?"closed":"open");
			nodeList.add(node);
		}
		
		return nodeList;
	}
	@Override
	public TbContentCategory addContentCategory(long parentId, String name) {
		//添加新节点
		TbContentCategory contentCategory = new TbContentCategory();
		contentCategory.setParentId(parentId);
		contentCategory.setName(name);
		//状态。可选值:1(正常),2(删除)
		contentCategory.setStatus(1);
		//排列序号，表示同级类目的展现次序，如数值相等则按名称次序排列。取值范围:大于零的整数
		contentCategory.setSortOrder(1);
		//该类目是否为父类目，1为true，0为false
		contentCategory.setIsParent(false);
		Date date = new Date();
		contentCategory.setCreated(date);
		contentCategory.setUpdated(date);
		contentCategoryMapper.insert(contentCategory);
		
		//判断父节点是否为叶子节点
		TbContentCategory parent = contentCategoryMapper.selectByPrimaryKey(parentId);
		if (!parent.getIsParent()) {
			//如果是叶子节点
			parent.setIsParent(true);
			contentCategoryMapper.updateByPrimaryKey(parent);
		}
		return contentCategory;
	}
	@Override
	public void updateContentCategory(long id, String name) {
		TbContentCategory contentCategory = contentCategoryMapper.selectByPrimaryKey(id);
		contentCategory.setName(name);
		contentCategoryMapper.updateByPrimaryKey(contentCategory);
	}
	@Override
	public void deleteContentCategory(long id) {
		TbContentCategory contentCategory = contentCategoryMapper.selectByPrimaryKey(id);
		if (contentCategory.getIsParent()) {
			//如果不是叶子节点
			TbContentCategoryExample example = new TbContentCategoryExample();
			Criteria criteria = example.createCriteria();
			criteria.andParentIdEqualTo(id);
			List<TbContentCategory> list = contentCategoryMapper.selectByExample(example);
			if (list != null && list.size() > 0) {
				for (TbContentCategory tbContentCategory : list) {
					deleteContentCategory(tbContentCategory.getId());
				}
			}
		}
		contentCategoryMapper.deleteByPrimaryKey(id);
		
	}
	
	/**
	 * 删除前要判断被删除节点的父节点是否还有其他节点
	 * 如果没有,需要将is_parnet属性置为false
	 */
	@Override
	public void deleteBeforeInit(long id) {
		TbContentCategory contentCategory = contentCategoryMapper.selectByPrimaryKey(id);
		TbContentCategoryExample example = new TbContentCategoryExample();
		Criteria criteria = example.createCriteria();
		criteria.andParentIdEqualTo(contentCategory.getParentId());
		List<TbContentCategory> list = contentCategoryMapper.selectByExample(example);
		if (list != null && list.size() == 1) {
			TbContentCategory parent = contentCategoryMapper.selectByPrimaryKey(contentCategory.getParentId());
			parent.setIsParent(false);
			contentCategoryMapper.updateByPrimaryKey(parent);
		}
	}

}
