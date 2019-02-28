package com.server.system.service.impl;

import com.server.redis.service.RedisService;
import com.server.system.pojo.User;
import com.server.system.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private RedisService redisService;
    @Override
    public int save(User record) throws Exception {
        if(!redisService.exists(record.getUsername())){
            redisService.set(record.getUsername(),record);
        }
        return 0;
    }

    @Override
    public int updatePassWord(User record) throws Exception {
        redisService.remove(record.getUsername());
        redisService.set(record.getUsername(),record);
        return 0;
    }

    @Override
    public User selectByUserName() throws Exception {
        User user= new User();
        if(redisService.exists(user.getUsername())){
            user=(User)redisService.get(user.getUsername());
        }else {
            user.setUsername("administrator");
            user.setPassword("123456");
        }
        return user;
    }
}
