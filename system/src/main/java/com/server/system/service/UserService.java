package com.server.system.service;

import com.server.system.pojo.User;

import java.util.List;
import java.util.Map;

public interface UserService {
	int deleteByPrimaryKey(String id);

    int insertSelective(User record);

    User selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(User record);
    
    Integer pageCount(Map<String, Object> map);
    
    List<User> findByPage(Map<String, Object> map);

	User get(String username);
}
