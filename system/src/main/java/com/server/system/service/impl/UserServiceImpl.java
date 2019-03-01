package com.server.system.service.impl;

import com.server.redis.service.RedisService;
import com.server.system.pojo.User;
import com.server.system.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@Cacheable("User")
public class UserServiceImpl implements UserService {

    private static String tableName="Users:";

    @Autowired
    private RedisService redisService;
    @Override
    public int save(User record) throws Exception {
        if(!redisService.exists(tableName+record.getUsername())){
            redisService.set(tableName+record.getUsername(),record);
        }
        return 0;
    }

    @Override
    public int updatePassWord(User record) throws Exception {
        redisService.remove(tableName+record.getUsername());
        redisService.set(tableName+record.getUsername(),record);
        return 0;
    }

    @Override
    public User selectByUserName() throws Exception {
        User user= new User();
        if(redisService.exists(tableName+user.getUsername())){
            user=(User)redisService.get(tableName+user.getUsername());
        }else {
            user.setUsername("administrator");
            user.setPassword("123456");
            redisService.set(tableName+user.getUsername(),user);
        }
        return user;
    }
}
