package com.server.system.service;

import com.server.system.pojo.User;
import org.springframework.stereotype.Service;

public interface UserService {
    int save(User record) throws Exception;

    int updatePassWord(User record)throws Exception;

    User selectByUserName()throws Exception;
}
