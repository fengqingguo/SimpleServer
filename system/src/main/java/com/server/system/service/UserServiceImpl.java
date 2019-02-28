package com.server.system.service;

import com.server.redis.service.RedisService;
import com.server.shiro.utils.PasswordHelper;
import com.server.system.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;

public class UserServiceImpl implements UserService {
    @Autowired
    private RedisService redisService;
    @Autowired
    private PasswordHelper helper;
    @Override
    public int save(User record) throws Exception {
        helper.encryptPassword(record);
        if(!redisService.exists(record.getUsername())){
            redisService.set(record.getUsername(),record);
        }
        return 0;
    }

    @Override
    public int updatePassWord(User record) throws Exception {
        helper.encryptPassword(record);
        redisService.remove(record.getUsername());
        redisService.set(record.getUsername(),record);
        return 0;
    }

    @Override
    public User selectByUserName(String username) throws Exception {
        return (User)redisService.get(username);
    }
}
