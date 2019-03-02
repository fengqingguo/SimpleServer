package com.server.shiro.realm;

import com.server.redis.service.RedisService;
import com.server.shiro.jwt.JwtConfig;
import com.server.shiro.jwt.JwtToken;
import com.server.shiro.jwt.JwtUtil;
import com.server.system.pojo.User;
import com.server.system.service.UserService;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.jsoup.helper.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
public class MyRealm extends AuthorizingRealm {

    private static final Logger LOGGER = LoggerFactory.getLogger(MyRealm.class);
    @Autowired
    private JwtConfig jwtConfig;
    @Autowired
    @Lazy
    private UserService userService;
    @Autowired
    private RedisService redisService;
    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 大坑！，必须重写此方法，不然Shiro会报错
     */
    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof JwtToken;
    }

    /**
     * 只有当需要检测用户权限的时候才会调用此方法，例如checkRole,checkPermission之类的
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        /*String username = JWTUtil.getUsername(principals.toString());
        User user = userService.getUser(username);*/
        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
       /* simpleAuthorizationInfo.addRole(user.getRole());
        Set<String> permission = new HashSet<>(Arrays.asList(user.getPermission().split(",")));
        simpleAuthorizationInfo.addStringPermissions(permission);*/
        return simpleAuthorizationInfo;
    }

    /**
     * 默认使用此方法进行用户名正确与否验证，错误抛出异常即可。
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken auth) throws AuthenticationException {
        String token = (String) auth.getCredentials();
        // 解密获得account，用于和数据库进行对比
        String account = JwtUtil.getClaim(token, jwtConfig.getAccount());
        // 帐号为空
        if (StringUtil.isBlank(account)) {
            throw new AuthenticationException("Token中帐号为空(The account in Token is empty.)");
        }
        // 查询用户是否存在
        User user = new User();
        try {
            user = userService.get(account);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (user == null) {
            throw new AuthenticationException("该帐号不存在(The account does not exist.)");
        }
        // 开始认证，要AccessToken认证通过，且Redis中存在RefreshToken，且两个Token时间戳一致
        if (jwtUtil.verify(token) && redisService.exists(jwtConfig.getPrefixShiroRefreshToken()+ account)) {
            // 获取RefreshToken的时间戳
            String currentTimeMillisRedis = redisService.get(jwtConfig.getPrefixShiroRefreshToken() + account).toString();
            // 获取AccessToken时间戳，与RefreshToken的时间戳对比
            if (JwtUtil.getClaim(token, jwtConfig.getCurrentTimeMillis()).equals(currentTimeMillisRedis)) {
                return new SimpleAuthenticationInfo(token, token, "userRealm");
            }
        }
        throw new AuthenticationException("Token已过期(Token expired or incorrect.)");
    }
}
