package com.server.web.controller.main;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.server.system.pojo.ResponseBean;
import com.server.system.service.UserService;
import com.wang.model.common.BaseDto;
import com.wang.model.valid.group.UserEditValidGroup;
import com.wang.model.valid.group.UserLoginValidGroup;
import com.wang.util.JedisUtil;
import com.wang.exception.CustomException;
import com.wang.exception.CustomUnauthorizedException;
import com.wang.model.UserDto;
import com.wang.model.common.Constant;
import com.wang.model.common.ResponseBean;
import com.wang.service.IUserService;
import com.wang.util.AesCipherUtil;
import com.wang.util.JwtUtil;
import com.wang.util.common.StringUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
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
public class UserController {

    /**
     * RefreshToken过期时间
     */
    @Value("${refreshTokenExpireTime}")
    private String refreshTokenExpireTime;
    @Autowired
    private UserService userService;

    /**
     * 登录授权
     * @param userDto
     * @return com.wang.model.common.ResponseBean
     * @author Wang926454
     * @date 2018/8/30 16:21
     */
    @PostMapping("/login")
    public ResponseBean login(String password, HttpServletResponse httpServletResponse) {
        // 查询数据库中的帐号信息

        // 密码进行AES解密
        String key = AesCipherUtil.deCrypto(userDtoTemp.getPassword());
        // 因为密码加密是以帐号+密码的形式进行加密的，所以解密后的对比是帐号+密码
        if (key.equals(userDto.getAccount() + userDto.getPassword())) {
            // 清除可能存在的Shiro权限信息缓存
            if (JedisUtil.exists(Constant.PREFIX_SHIRO_CACHE + userDto.getAccount())) {
                JedisUtil.delKey(Constant.PREFIX_SHIRO_CACHE + userDto.getAccount());
            }
            // 设置RefreshToken，时间戳为当前时间戳，直接设置即可(不用先删后设，会覆盖已有的RefreshToken)
            String currentTimeMillis = String.valueOf(System.currentTimeMillis());
            JedisUtil.setObject(Constant.PREFIX_SHIRO_REFRESH_TOKEN + userDto.getAccount(), currentTimeMillis, Integer.parseInt(refreshTokenExpireTime));
            // 从Header中Authorization返回AccessToken，时间戳为当前时间戳
            String token = JwtUtil.sign(userDto.getAccount(), currentTimeMillis);
            httpServletResponse.setHeader("Authorization", token);
            httpServletResponse.setHeader("Access-Control-Expose-Headers", "Authorization");
            return new ResponseBean(HttpStatus.OK.value(), "登录成功(Login Success.)", null);
        } else {
            throw new CustomUnauthorizedException("帐号或密码错误(Account or Password Error.)");
        }
    }

    /**
     * 更新用户
     * @param userDto
     * @return java.util.Map<java.lang.String,java.lang.Object>
     * @author Wang926454
     * @date 2018/8/30 10:42
     */
    @PutMapping
    @RequiresPermissions(logical = Logical.AND, value = {"user:edit"})
    public ResponseBean update(@Validated(UserEditValidGroup.class) @RequestBody UserDto userDto) {
        // 查询数据库密码
        UserDto userDtoTemp = new UserDto();
        userDtoTemp.setAccount(userDto.getAccount());
        userDtoTemp = userService.selectOne(userDtoTemp);
        if (userDtoTemp == null) {
            throw new CustomUnauthorizedException("该帐号不存在(Account not exist.)");
        } else {
            userDto.setId(userDtoTemp.getId());
        }
        // FIXME: 如果不一样就说明用户修改了密码，重新加密密码(这个处理不太好，但是没有想到好的处理方式)
        if (!userDtoTemp.getPassword().equals(userDto.getPassword())) {
            // 密码以帐号+密码的形式进行AES加密
            if (userDto.getPassword().length() > Constant.PASSWORD_MAX_LEN) {
                throw new CustomException("密码最多8位(Password up to 8 bits.)");
            }
            String key = AesCipherUtil.enCrypto(userDto.getAccount() + userDto.getPassword());
            userDto.setPassword(key);
        }
        int count = userService.updateByPrimaryKeySelective(userDto);
        if (count <= 0) {
            throw new CustomException("更新失败(Update Failure)");
        }
        return new ResponseBean(HttpStatus.OK.value(), "更新成功(Update Success)", userDto);
    }

    /**
     * 剔除在线用户
     * @param id
     * @return com.wang.model.common.ResponseBean
     * @author Wang926454
     * @date 2018/9/6 10:20
     */
    @DeleteMapping("/online/{id}")
    @RequiresPermissions(logical = Logical.AND, value = {"user:edit"})
    public ResponseBean deleteOnline(@PathVariable("id") Integer id) {
        UserDto userDto = userService.selectByPrimaryKey(id);
        if (JedisUtil.exists(Constant.PREFIX_SHIRO_REFRESH_TOKEN + userDto.getAccount())) {
            if (JedisUtil.delKey(Constant.PREFIX_SHIRO_REFRESH_TOKEN + userDto.getAccount()) > 0) {
                return new ResponseBean(HttpStatus.OK.value(), "剔除成功(Delete Success)", null);
            }
        }
        throw new CustomException("剔除失败，Account不存在(Deletion Failed. Account does not exist.)");
    }
}