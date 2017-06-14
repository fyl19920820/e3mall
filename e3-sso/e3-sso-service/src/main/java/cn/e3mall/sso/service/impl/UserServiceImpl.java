package cn.e3mall.sso.service.impl;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.apache.zookeeper.Op.Create;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import cn.e3mall.common.jedis.JedisClient;
import cn.e3mall.common.pojo.E3Result;
import cn.e3mall.common.utils.JsonUtils;
import cn.e3mall.mapper.TbUserMapper;
import cn.e3mall.pojo.TbUser;
import cn.e3mall.pojo.TbUserExample;
import cn.e3mall.pojo.TbUserExample.Criteria;
import cn.e3mall.sso.service.UserService;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private TbUserMapper userMapper;
	
	@Autowired
	private JedisClient jedisClient;
	
	@Value("${SESSION_EXPIRE}")
	private Integer SESSION_EXPIRE;
//		参数：
//		String data	要校验的数据
//		int type	要校验的数据类型 1：用户名 2：手机号 3:邮箱
	@Override
	public E3Result checkUser(String data, int type) {
		TbUserExample example = new TbUserExample();
		Criteria criteria = example.createCriteria();
		if (type == 1) {
			criteria.andUsernameEqualTo(data);
		}else if (type == 2) {
			criteria.andPhoneEqualTo(data);
		}else if (type ==3) {
			criteria.andEmailEqualTo(data);
		} else {
			return E3Result.build(400, "数据错误!");
		}
		List<TbUser> list = userMapper.selectByExample(example);
		if (list != null && list.size() > 0) {
			return E3Result.ok(false);
		}else {
			return E3Result.ok(true);
		}
	}
	@Override
	public E3Result register(TbUser user) {
		if (StringUtils.isBlank(user.getUsername())) {
			return E3Result.build(400, "用户名不能为空");
		}
		if (StringUtils.isBlank(user.getPassword())) {
			return E3Result.build(400, "密码不能为空");
		}
		E3Result e3Result = checkUser(user.getUsername(), 1);
		if (!(boolean)e3Result.getData()) {
			return E3Result.build(400, "用户名已存在");
		}
		if (StringUtils.isNotBlank(user.getPhone())) {
			e3Result = checkUser(user.getPhone(), 2);
			if (!(boolean)e3Result.getData()) {
				return E3Result.build(400, "电话已存在");
			}
		}
		if (StringUtils.isNotBlank(user.getEmail())) {
			e3Result = checkUser(user.getEmail(), 3);
			if (!(boolean)e3Result.getData()) {
				return E3Result.build(400, "邮箱已存在");
			}
		}
		Date date = new Date();
		user.setCreated(date);
		user.setUpdated(date);
		//密码进行md5加密
		String PWD = DigestUtils.md5DigestAsHex(user.getPassword().getBytes());
		user.setPassword(PWD);
		userMapper.insert(user);
		return E3Result.ok();
	}
	@Override
	public E3Result login(String username, String password) {
		TbUserExample example = new TbUserExample();
		Criteria criteria = example.createCriteria();
		criteria.andUsernameEqualTo(username);
		List<TbUser> list = userMapper.selectByExample(example);
		//判断用户名是否存在
		if (list == null || list.size() == 0) {
			return E3Result.build(400, "用户名或密码错误");
		}
		TbUser user = list.get(0);
		//判断密码是否相同
		if (!user.getPassword().equals(DigestUtils.md5DigestAsHex(password.getBytes()))) {
			return E3Result.build(400, "用户名或密码错误");
		}
		String sessionId = UUID.randomUUID().toString();
		user.setPassword(null);
		jedisClient.set("USER:"+sessionId, JsonUtils.objectToJson(user));
		jedisClient.expire("USER:"+sessionId, SESSION_EXPIRE);
		return E3Result.ok(sessionId);
	}
	@Override
	public E3Result getUserByToken(String token) {
		String userString = jedisClient.get("USER:"+token);
		if (StringUtils.isNotBlank(userString)) {
			TbUser user = JsonUtils.jsonToPojo(userString, TbUser.class);
			//重置有效时间
			jedisClient.expire("USER:"+token, SESSION_EXPIRE);
			return E3Result.ok(user);
		}
		return E3Result.build(400, "用户已过期,请重新登录");
	}

}
