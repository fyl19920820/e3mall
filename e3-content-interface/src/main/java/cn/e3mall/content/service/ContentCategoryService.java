package cn.e3mall.content.service;

import java.util.List;

import cn.e3mall.common.pojo.EasyUITreeNode;
import cn.e3mall.pojo.TbContentCategory;

public interface ContentCategoryService {
	List<EasyUITreeNode> getContentCategoryListByParentId(long parentId);
	TbContentCategory addContentCategory(long parentId,String name);
	void updateContentCategory(long id,String name);
	void deleteContentCategory(long id);
	void deleteBeforeInit(long id);
}
