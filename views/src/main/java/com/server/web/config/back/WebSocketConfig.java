package com.server.web.config.back;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.lang.Nullable;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;

/**
 * WebSocket配置
 *
 * @author lnkToKing
 */
//@Configuration
/*
 * 开启使用STOMP协议来传输基于代理（message broker）的消息
 * 启用后控制器支持作用@MessgeMapping注解
 */
//@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /*@Value("${token-secret-key}")
    private String tokenSecretKey;*/

    //注册STOMP协议节点并映射url
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        //添加连接节点
        registry.addEndpoint("/endpoint").addInterceptors(new HandshakeInterceptor() {
            @Override
            public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse serverHttpResponse,
                                           WebSocketHandler webSocketHandler, Map<String, Object> map) throws Exception {
                ServletServerHttpRequest req = (ServletServerHttpRequest) request;
                //获取token认证
                String token = req.getServletRequest().getParameter("token");
                //解析token获取用户信息
                Principal user = parseToken(token);
                if(user == null){   //如果token认证失败user为null，返回false拒绝握手
                    return false;
                }
                //保存认证用户
                map.put("user", user);
                return true;
            }

            @Override
            public void afterHandshake(ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse, WebSocketHandler webSocketHandler, @Nullable Exception e) {

            }
        }).setHandshakeHandler(new DefaultHandshakeHandler() {
            @Override
            protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
                //设置认证用户
                return (Principal)attributes.get("user");
            }
        }).setAllowedOrigins("*") //允许跨域
          .withSockJS();  //指定使用SockJS协议
    }

    /**
     * 根据token认证授权
     *
     * @param token
     */
    private Principal parseToken(String token) {
        //TODO 解析token并获取认证用户信息
        return null;
    }
}
