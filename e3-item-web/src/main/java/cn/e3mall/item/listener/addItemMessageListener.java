package cn.e3mall.item.listener;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import cn.e3mall.item.pojo.Item;
import cn.e3mall.pojo.TbItem;
import cn.e3mall.pojo.TbItemDesc;
import cn.e3mall.service.ItemService;
import freemarker.template.Configuration;
import freemarker.template.Template;

public class addItemMessageListener implements MessageListener {
	@Autowired
	private ItemService itemService;
	
	@Autowired
	private FreeMarkerConfigurer freeMarkerConfigurer;

	@Value("${html_gen_path}")
	private String html_gen_path;
	
	@Override
	public void onMessage(Message message) {
		try {
			Thread.sleep(100);
			//通过id获取商品与商品详情对象
			TextMessage textMessage = (TextMessage) message;
			String text = textMessage.getText();
			long id = new Long(text);
			TbItem tbItem = itemService.getItemById(id);
			Item item = new Item(tbItem);
			TbItemDesc tbItemDesc = itemService.queryItemDescById(id);
			//通过freemarker生成静态页面
			Configuration configuration = freeMarkerConfigurer.getConfiguration();
			Template template = configuration.getTemplate("item.ftl");
			Map data = new HashMap<>();
			data.put("item", item);
			data.put("itemDesc", tbItemDesc);
			Writer out = new FileWriter(new File(html_gen_path+id+".html"));
			template.process(data, out);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


}
