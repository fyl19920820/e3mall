package cn.e3mall.content.service;

import java.util.List;

import cn.e3mall.common.pojo.EasyUIDataGaridResult;
import cn.e3mall.pojo.TbContent;

public interface ContentService {
	EasyUIDataGaridResult getContentListByCategoryId(long categoryId,int page,int rows);
	void addContent(TbContent content);
	void updateContent(TbContent content);
	void deleteContent(String ids);
	List<TbContent> getContentListByCategoryId(long categoryId);
}
