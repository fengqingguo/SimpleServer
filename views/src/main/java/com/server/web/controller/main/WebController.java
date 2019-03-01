package com.server.web.controller.main;

import com.server.common.exception.CustomUnauthorizedException;
import com.server.redis.service.RedisService;
import com.server.shiro.jwt.JwtConfig;
import com.server.shiro.jwt.JwtUtil;
import com.server.shiro.utils.AesCipherUtil;
import com.server.system.pojo.ResponseBean;
import com.server.system.pojo.User;
import com.server.system.service.UserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.UnauthorizedException;
import org.apache.shiro.authz.annotation.*;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

@RestController
public class WebController {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebController.class);

    private UserService userService;
    @Autowired
    private JwtConfig jwtConfig;
    @Autowired
    private RedisService redisService;
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    public void setService(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseBean login(@RequestParam("password") String password,HttpServletResponse httpServletResponse) {
        User userBean = null;
        try {
            userBean = userService.selectByUserName();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 清除可能存在的Shiro权限信息缓存
        if (redisService.exists(jwtConfig.getPrefixShiroCache() + userBean.getUsername())) {
            redisService.remove(jwtConfig.getPrefixShiroCache() + userBean.getUsername());
        }
        // 设置RefreshToken，时间戳为当前时间戳，直接设置即可(不用先删后设，会覆盖已有的RefreshToken)
        String currentTimeMillis = String.valueOf(System.currentTimeMillis());
        redisService.set(jwtConfig.getPrefixShiroRefreshToken() +userBean.getUsername(), currentTimeMillis, jwtConfig.getRefreshTokenExpireTime());
        // 从Header中Authorization返回AccessToken，时间戳为当前时间戳
        String token = jwtUtil.sign(userBean.getUsername(), currentTimeMillis);
        httpServletResponse.setHeader("Authorization", token);
        httpServletResponse.setHeader("Access-Control-Expose-Headers", "Authorization");
        return new ResponseBean(HttpStatus.OK.value(), "登录成功(Login Success.)", token);
        /*// 密码进行AES解密
        String key = AesCipherUtil.deCrypto(userBean.getPassword(),jwtConfig.getEncryptAESKey());
        // 因为密码加密是以帐号+密码的形式进行加密的，所以解密后的对比是帐号+密码
        if (key.equals(userBean.getUsername() + userBean.getPassword())) {
        } else {
            throw new CustomUnauthorizedException("帐号或密码错误(Account or Password Error.)");
        }*/
    }

    @GetMapping("/article")
    public ResponseBean article() {
        Subject subject = SecurityUtils.getSubject();
        if (subject.isAuthenticated()) {
            return new ResponseBean(200, "You are already logged in", null);
        } else {
            return new ResponseBean(200, "You are guest", null);
        }
    }

    @GetMapping("/require_auth")
    @RequiresAuthentication
    public ResponseBean requireAuth() {
        return new ResponseBean(200, "You are authenticated", null);
    }

    @GetMapping("/require_role")
    @RequiresRoles("admin")
    public ResponseBean requireRole() {
        return new ResponseBean(200, "You are visiting require_role", null);
    }

    @GetMapping("/require_permission")
    @RequiresPermissions(logical = Logical.AND, value = {"view", "edit"})
    public ResponseBean requirePermission() {
        return new ResponseBean(200, "You are visiting permission require edit,view", null);
    }

    @RequestMapping(path = "/401")
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseBean unauthorized() {
        return new ResponseBean(401, "Unauthorized", null);
    }
}
