package com.chen.LeoBlog.interceptors;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.servlet.ServletUtil;
import cn.hutool.json.JSONUtil;
import com.chen.LeoBlog.constant.MDCKey;
import com.chen.LeoBlog.constant.RedisConstant;
import com.chen.LeoBlog.dto.UserDTO;
import com.chen.LeoBlog.po.User;
import com.chen.LeoBlog.utils.WebUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.TimeUnit;


@Slf4j

public class RefreshTTLInterceptor implements HandlerInterceptor {

    private final StringRedisTemplate redisTemplate;

    public RefreshTTLInterceptor(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public synchronized boolean preHandle(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response, Object handler) {
        if ("OPTIONS".equals(request.getMethod()) || "/error".equals(request.getRequestURI())) {
            return true;
        }
        //从请求头中拿到token
        String token = request.getHeader("Authorization");
        //如果没有传递token，直接放行
        if (token == null || StrUtil.isBlank(token)) return true;
        //根据token查询redis中对应的信息
        Object user = new User();
        String key = RedisConstant.USER_LOGIN + token;
        try {
            user = redisTemplate.opsForValue().get(key);
            if (user == null) return true;
            Long ttl = redisTemplate.getExpire(key, TimeUnit.HOURS);
            if (ttl == null) {
                // 如果token已经过期，直接返回401，告诉前端token过期
                WebUtil.responseMsg(response, HttpStatus.UNAUTHORIZED.value(), "token过期");
                return false;
            }
            //最后一天刷新token的过期时间
            if (ttl < 24) redisTemplate.expire(key, RedisConstant.USER_LOGIN_TTL, TimeUnit.DAYS);
        } catch (Exception e) {
            log.error("redis异常:[{}]", key, e);
        }
        UserDTO loginUser = JSONUtil.toBean(user.toString(), UserDTO.class);
        // 获取客户端IP
        String clientIP = ServletUtil.getClientIP(request);
        loginUser.setIP(clientIP);
        // 将用户ID存入MDC
        MDC.put(MDCKey.UID, loginUser.getUserId().toString());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
//        Local.removeUser();
    }


}

