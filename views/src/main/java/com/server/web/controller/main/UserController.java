package com.server.web.controller.main;


import com.server.redis.service.RedisService;
import com.server.shiro.jwt.JwtUtil;
import com.server.shiro.utils.Constant;
import com.server.shiro.utils.CustomException;
import com.server.system.pojo.ResponseBean;
import com.server.system.pojo.User;
import com.server.system.service.UserService;
import com.server.web.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * UserController
 * @author Wang926454
 * @date 2018/8/29 15:45
 */
@RestController
@RequestMapping("/user")
@PropertySource("classpath:config.properties")
public class UserController extends BaseController {

    /**
     * RefreshToken过期时间
     */
    @Value("${refreshTokenExpireTime}")
    private String refreshTokenExpireTime;
    @Autowired
    private UserService userService;
    @Autowired
    private RedisService redisService;

    /**
     * 登录授权
     * @param
     * @return com.wang.model.common.ResponseBean
     * @author Wang926454
     * @date 2018/8/30 16:21
     */
    @PostMapping("/login")
    public ResponseBean login( @RequestParam("password") String password, HttpServletResponse httpServletResponse) {
        // 查询数据库中的帐号信息
        User user=new User();
        try {
            user=userService.selectByUserName();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 因为密码加密是以帐号+密码的形式进行加密的，所以解密后的对比是帐号+密码
        if (user.getPassword().equals(password)){
            // 清除可能存在的Shiro权限信息缓存
            if (redisService.exists(Constant.PREFIX_SHIRO_CACHE + user.getUsername())) {
                redisService.remove(Constant.PREFIX_SHIRO_CACHE + user.getUsername());
            }
            // 设置RefreshToken，时间戳为当前时间戳，直接设置即可(不用先删后设，会覆盖已有的RefreshToken)
            String currentTimeMillis = String.valueOf(System.currentTimeMillis());
            redisService.set(Constant.PREFIX_SHIRO_REFRESH_TOKEN + user.getUsername(), currentTimeMillis, Long.parseLong(refreshTokenExpireTime));
            // 从Header中Authorization返回AccessToken，时间戳为当前时间戳
            String token = JwtUtil.sign(user.getUsername(), currentTimeMillis);
            httpServletResponse.setHeader("Authorization", token);
            httpServletResponse.setHeader("Access-Control-Expose-Headers", "Authorization");
            return new ResponseBean(HttpStatus.OK.value(), "登录成功(Login Success.)", null);
        } else {
            throw new CustomException("帐号或密码错误(Account or Password Error.)");
        }
    }
    /**
     * 更新用户
     * @param
     * @return java.util.Map<java.lang.String,java.lang.Object>
     * @author Wang926454
     * @date 2018/8/30 10:42
     */
    @PostMapping(value = "test")
    public ResponseBean update(HttpServletResponse httpServletResponse) {
        return new ResponseBean(HttpStatus.OK.value(), "更新成功(Update Success)",null);
    }
    /**
     * 更新用户
     * @param
     * @return java.util.Map<java.lang.String,java.lang.Object>
     * @author Wang926454
     * @date 2018/8/30 10:42
     */
    @PutMapping
    public ResponseBean update(@RequestParam("password") String password,@RequestParam("password") String oldpassword, HttpServletResponse httpServletResponse) {
        // 查询数据库密码
        User user=new User();
        try {
            user=userService.selectByUserName();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // FIXME: 如果不一样就说明用户修改了密码，重新加密密码(这个处理不太好，但是没有想到好的处理方式)
        if (user.getPassword().equals(oldpassword)) {
            user.setPassword(password);
        }
        int count = 0;
        try {
            count = userService.updatePassWord(user);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (count <= 0) {
            throw new CustomException("更新失败(Update Failure)");
        }
        return new ResponseBean(HttpStatus.OK.value(), "更新成功(Update Success)",null);
    }
}