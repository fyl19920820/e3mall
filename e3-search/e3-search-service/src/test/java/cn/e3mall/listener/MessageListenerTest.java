package cn.e3mall.listener;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

public class MessageListenerTest implements MessageListener {


	@Override
	public void onMessage(Message message) {
		TextMessage textMessage = (TextMessage) message;
		try {
			String string = textMessage.getText();
			System.out.println(string);
		} catch (JMSException e) {
			
			e.printStackTrace();
		}
	}

}
