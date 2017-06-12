package cn.e3mall.service;

import cn.e3mall.common.pojo.EasyUIDataGaridResult;
import cn.e3mall.pojo.TbItem;
import cn.e3mall.pojo.TbItemDesc;

public interface ItemService {
	public TbItem getItemById(long id);
	public EasyUIDataGaridResult getItemList(int page,int rows);
	public void addItem(TbItem item,String desc);
	public TbItemDesc queryItemDescById(long id);
	public void updateItem(TbItem item);
	public void deleteItem(String ids);
	public void instockItem(String ids);
	public void reshelfItem(String ids);
}
