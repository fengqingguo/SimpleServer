package com.server.web.controller.main;

import com.server.redis.service.RedisService;
import com.server.shiro.jwt.JwtConfig;
import com.server.shiro.jwt.JwtUtil;
import com.server.system.pojo.ResponseBean;
import com.server.system.pojo.User;
import com.server.system.service.UserService;
import com.server.system.util.PasswordHelper;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ByteSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@RestController
public class WebController {

    private static final Logger logger = LoggerFactory.getLogger(WebController.class);

    private UserService userService;
    @Autowired
    private JwtConfig jwtConfig;
    @Autowired
    private RedisService redisService;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private PasswordHelper helper;

    @Autowired
    public void setService(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseBean login(@RequestParam("username") String username,
                              @RequestParam("password") String password,HttpServletResponse httpServletResponse) {
        User userBean = null;
        try {
            userBean = userService.get(username);
            String pd = new SimpleHash(helper.algorithmName, password, ByteSource.Util.bytes(userBean.getCredentialsSalt()),helper.hashIterations).toHex();
            if(userBean==null){
                return new ResponseBean(HttpStatus.UNAUTHORIZED.value(), "登录失败(用户不存在)", null);
            }else if(!pd.equals(userBean.getPassword())){
                return new ResponseBean(HttpStatus.UNAUTHORIZED.value(), "登录失败(密码错误)", null);
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
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseBean(HttpStatus.INTERNAL_SERVER_ERROR.value(), "系统错误,请重试!", null);
        }
    }

    @RequestMapping(value = "editpwd", method = RequestMethod.POST)
    @ResponseBody
    public String editpwd(@RequestParam("oldpwd")String oldpwd, @RequestParam("newpwd")String newpwd,HttpServletRequest request) {
        String token = SecurityUtils.getSubject().getPrincipal().toString();
        String username = JwtUtil.getClaim(token, jwtConfig.getAccount());
        User user = userService.get(username);
        String oldPassword = new SimpleHash(helper.algorithmName, oldpwd, ByteSource.Util.bytes(user.getCredentialsSalt()),helper.hashIterations).toHex();
        if (user.getPassword().equals(oldPassword)) {
            try {
                user.setPassword(newpwd);
                user.setEdittime(new Date());
                userService.updateByPrimaryKeySelective(user);
                Subject subject = SecurityUtils.getSubject();
                if (subject.isAuthenticated()) {
                    subject.logout();
                }
                return "{'result':true,'msg':'密码修改成功！'}";
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                return "{'result':false,'msg':'密码修改失败！'}";
            }
        } else {
            return "{'result':false,'msg':'旧密码输入有误！'}";
        }
    }

    @RequestMapping(path = "/401")
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseBean unauthorized() {
        return new ResponseBean(401, "Unauthorized", null);
    }
}
