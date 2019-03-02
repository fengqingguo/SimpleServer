package com.server.system.service;

import com.server.system.mapper.UserMapper;
import com.server.system.pojo.User;
import com.server.system.util.PasswordHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service("userService")
public class UserServiceImpl implements UserService {
	@Autowired
	private UserMapper mapper;
	@Autowired
	private PasswordHelper helper;
	@Override
	public int deleteByPrimaryKey(String id) {
		return mapper.deleteByPrimaryKey(id);
	}

	@Override
	public int insertSelective(User record) {
		helper.encryptPassword(record);
		return mapper.insertSelective(record);
	}

	@Override
	public User selectByPrimaryKey(String id) {
		return mapper.selectByPrimaryKey(id);
	}

	@Override
	public int updateByPrimaryKeySelective(User record) {
		helper.encryptPassword(record);
		return mapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public Integer pageCount(Map<String, Object> map) {
		return mapper.pageCount(map);
	}

	@Override
	public List<User> findByPage(Map<String, Object> map) {
		return mapper.findByPage(map);
	}

	@Override
	public User get(String username) {
		return mapper.selectByUserName(username);
	}

}
