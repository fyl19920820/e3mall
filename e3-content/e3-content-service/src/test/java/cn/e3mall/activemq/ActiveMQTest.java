package cn.e3mall.activemq;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.junit.Test;

public class ActiveMQTest {

	@Test
	public void queueProducerTest() throws Exception{
//		第一步：创建ConnectionFactory对象，需要指定服务端ip及端口号。
		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://192.168.119.135:61616");
//		第二步：使用ConnectionFactory对象创建一个Connection对象。
		Connection connection = connectionFactory.createConnection();
//		第三步：开启连接，调用Connection对象的start方法。
		connection.start();
//		第四步：使用Connection对象创建一个Session对象。
		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
//		第五步：使用Session对象创建一个Destination对象（topic、queue），此处创建一个Queue对象。
		Queue queue = session.createQueue("test-queue");
//		第六步：使用Session对象创建一个Producer对象。
		MessageProducer producer = session.createProducer(queue);
//		第七步：创建一个Message对象，创建一个TextMessage对象。
		TextMessage message = new ActiveMQTextMessage();
		message.setText("hello queue");
//		第八步：使用Producer对象发送消息。
		producer.send(message);
//		第九步：关闭资源。
		producer.close();
		session.close();
		connection.close();

	}
	
	@Test
	public void queueConsumerTest() throws Exception{
//		第一步：创建一个ConnectionFactory对象。
		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://192.168.119.135:61616");
//		第二步：从ConnectionFactory对象中获得一个Connection对象。
		Connection connection = connectionFactory.createConnection();
//		第三步：开启连接。调用Connection对象的start方法。
		connection.start();
//		第四步：使用Connection对象创建一个Session对象。
		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
//		第五步：使用Session对象创建一个Destination对象。和发送端保持一致queue，并且队列的名称一致。
		Queue queue = session.createQueue("test-queue");
//		第六步：使用Session对象创建一个Consumer对象。
		MessageConsumer messageConsumer = session.createConsumer(queue);
//		第七步：接收消息。
		messageConsumer.setMessageListener(new MessageListener() {
			
			@Override
			public void onMessage(Message message) {
				TextMessage textMessage = (TextMessage) message;
				String string = null;
				try {
//					第八步：打印消息。
					string = textMessage.getText();
				} catch (JMSException e) {
					
					e.printStackTrace();
				}
				System.out.println(string);
			}
		});
		System.out.println("等待接受消息");
		System.in.read();
		System.out.println("接受完毕");
//		第九步：关闭资源
		messageConsumer.close();
		session.close();
		connection.close();
	}
	
	@Test
	public void topicProducerTest() throws Exception{
		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://192.168.119.135:61616");
		Connection connection = connectionFactory.createConnection();
		connection.start();
		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		Topic topic = session.createTopic("test-topic");
		MessageProducer producer = session.createProducer(topic);
		TextMessage textMessage = session.createTextMessage("hello topic");
		producer.send(textMessage);
		producer.close();
		session.close();
		connection.close();
	}
	
	@Test
	public void topicConsumerTest() throws Exception{
		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://192.168.119.135:61616");
		Connection connection = connectionFactory.createConnection();
		connection.start();
		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		Topic topic = session.createTopic("test-topic");
		MessageConsumer consumer = session.createConsumer(topic);
		consumer.setMessageListener(new MessageListener() {
			
			@Override
			public void onMessage(Message message) {
				TextMessage textMessage = (TextMessage) message;
				String string = null;
				try {
					string = textMessage.getText();
				} catch (JMSException e) {
					
					e.printStackTrace();
				}
				System.out.println(string);
			}
		});
		System.out.println("准备接受数据3");
		System.in.read();
		System.out.println("接受完毕3");
		consumer.close();
		session.close();
		connection.close();
	}
}
