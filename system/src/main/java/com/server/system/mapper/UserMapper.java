package com.server.system.mapper;

import com.server.system.pojo.User;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;
import java.util.Map;

@CacheConfig(cacheNames = "Users")
public interface UserMapper {
    @CacheEvict(key = "#p0")
    int deleteByPrimaryKey(String id);

    int insertSelective(User record);

    User selectByPrimaryKey(String id);
    @CacheEvict(key = "#record.username")
    int updateByPrimaryKeySelective(User record);

	Integer pageCount(Map<String, Object> map);

	List<User> findByPage(Map<String, Object> map);
    @Cacheable(key = "#p0")
	User selectByUserName(String username);
}