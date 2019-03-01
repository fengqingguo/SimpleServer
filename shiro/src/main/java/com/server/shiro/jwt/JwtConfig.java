package com.server.shiro.jwt;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:jwt.properties")
public class JwtConfig {

    @Value("${jwt.encryptAESKey}")
    private String encryptAESKey;
    @Value("${jwt.encryptJWTKey}")
    private String encryptJWTKey;
    @Value("${jwt.token.head}")
    private String tokenHead;
    @Value("${jwt.accessTokenExpireTime}")
    private Long accessTokenExpireTime;
    @Value("${jwt.refreshTokenExpireTime}")
    private Long refreshTokenExpireTime;
    @Value("${jwt.shiroCacheExpireTime}")
    private Long shiroCacheExpireTime;
    @Value("${jwt.prefixShiroCache}")
    private String prefixShiroCache;
    @Value("${jwt.prefixShiroAccessToken}")
    private String prefixShiroAccessToken;
    @Value("${jwt.prefixShiroRefreshToken}")
    private String prefixShiroRefreshToken;
    @Value("${jwt.account}")
    private String account;
    @Value("${jwt.currentTimeMillis}")
    private String currentTimeMillis;
    @Value("${jwt.passwordMaxLen}")
    private Integer passwordMaxLen;

    public String getEncryptAESKey() {
        return encryptAESKey;
    }

    public void setEncryptAESKey(String encryptAESKey) {
        this.encryptAESKey = encryptAESKey;
    }

    public String getEncryptJWTKey() {
        return encryptJWTKey;
    }

    public void setEncryptJWTKey(String encryptJWTKey) {
        this.encryptJWTKey = encryptJWTKey;
    }

    public String getTokenHead() {
        return tokenHead;
    }

    public void setTokenHead(String tokenHead) {
        this.tokenHead = tokenHead;
    }

    public Long getAccessTokenExpireTime() {
        return accessTokenExpireTime;
    }

    public void setAccessTokenExpireTime(Long accessTokenExpireTime) {
        this.accessTokenExpireTime = accessTokenExpireTime;
    }

    public Long getRefreshTokenExpireTime() {
        return refreshTokenExpireTime;
    }

    public void setRefreshTokenExpireTime(Long refreshTokenExpireTime) {
        this.refreshTokenExpireTime = refreshTokenExpireTime;
    }

    public Long getShiroCacheExpireTime() {
        return shiroCacheExpireTime;
    }

    public void setShiroCacheExpireTime(Long shiroCacheExpireTime) {
        this.shiroCacheExpireTime = shiroCacheExpireTime;
    }

    public String getPrefixShiroCache() {
        return prefixShiroCache;
    }

    public void setPrefixShiroCache(String prefixShiroCache) {
        this.prefixShiroCache = prefixShiroCache;
    }

    public String getPrefixShiroAccessToken() {
        return prefixShiroAccessToken;
    }

    public void setPrefixShiroAccessToken(String prefixShiroAccessToken) {
        this.prefixShiroAccessToken = prefixShiroAccessToken;
    }

    public String getPrefixShiroRefreshToken() {
        return prefixShiroRefreshToken;
    }

    public void setPrefixShiroRefreshToken(String prefixShiroRefreshToken) {
        this.prefixShiroRefreshToken = prefixShiroRefreshToken;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getCurrentTimeMillis() {
        return currentTimeMillis;
    }

    public void setCurrentTimeMillis(String currentTimeMillis) {
        this.currentTimeMillis = currentTimeMillis;
    }

    public Integer getPasswordMaxLen() {
        return passwordMaxLen;
    }

    public void setPasswordMaxLen(Integer passwordMaxLen) {
        this.passwordMaxLen = passwordMaxLen;
    }
}
