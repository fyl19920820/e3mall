package cn.e3mall.search.listener;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.springframework.beans.factory.annotation.Autowired;

import cn.e3mall.search.service.SearchItemService;

public class AddItemMessageListener implements MessageListener {

	@Autowired
	private SearchItemService searchItemService;

	@Override
	public void onMessage(Message message) {
		try {
			if (message instanceof TextMessage) {
				Thread.sleep(100);
				TextMessage textMessage = (TextMessage) message;
				String itemId = textMessage.getText();
				long id = new Long(itemId);
				searchItemService.importItemById(id);
			}
		
		} catch (Exception e) {
			
			e.printStackTrace();
		}
	}

}
