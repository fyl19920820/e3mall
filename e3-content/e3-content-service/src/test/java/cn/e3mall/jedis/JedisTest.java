package cn.e3mall.jedis;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import cn.e3mall.common.jedis.JedisClient;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;

public class JedisTest {

	@Test
	public void jedisTest(){
		Jedis jedis = new Jedis("192.168.119.131", 6379);
		jedis.set("a", "10");
		String string = jedis.get("a");
		System.out.println(string);
		jedis.close();
	}
	
	@Test
	public void jedispoolTest(){
		JedisPool jedisPool = new JedisPool("192.168.119.131", 6379);
		Jedis jedis = jedisPool.getResource();
		jedis.set("a", "5");
		String string = jedis.get("a");
		System.out.println(string);
		jedis.close();
		jedisPool.close();
	}
	
	@Test
	public void jedisclusterTest(){
		Set<HostAndPort> set = new HashSet<>();
		set.add(new HostAndPort("192.168.119.131", 7001));
		set.add(new HostAndPort("192.168.119.131", 7002));
		set.add(new HostAndPort("192.168.119.131", 7003));
		set.add(new HostAndPort("192.168.119.131", 7004));
		set.add(new HostAndPort("192.168.119.131", 7005));
		set.add(new HostAndPort("192.168.119.131", 7006));
		JedisCluster jedisCluster = new JedisCluster(set);
		jedisCluster.set("a", "7");
		String string = jedisCluster.get("a");
		System.out.println(string);
		jedisCluster.close();
	}
	
	@Test
	public void jedispoolSpringTest(){
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:spring/applicationContext-redis.xml");
		JedisClient client = applicationContext.getBean(JedisClient.class);
		client.set("a", "8");
		String string = client.get("a");
		System.out.println(string);
	}
}
