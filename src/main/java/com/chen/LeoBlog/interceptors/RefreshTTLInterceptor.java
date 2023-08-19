package com.chen.LeoBlog.interceptors;

import cn.hutool.core.util.StrUtil;
import com.chen.LeoBlog.constant.MDCKey;
import com.chen.LeoBlog.constant.RedisConstant;
import com.chen.LeoBlog.utils.JWTUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.TimeUnit;

import static com.chen.LeoBlog.constant.RedisConstant.USER_REFRESH_TTL;


@Slf4j
@Component
public class RefreshTTLInterceptor implements HandlerInterceptor {
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public synchronized boolean preHandle(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response, Object handler) {
        if ("OPTIONS".equals(request.getMethod()) || "/error".equals(request.getRequestURI())) {
            return true;
        }
        //从请求头中拿到token
        String token = request.getHeader("Authorization");
        //如果没有传递token，直接放行
        if (token == null || StrUtil.isBlank(token)) return true;
        renewalTokenIfNecessary(token);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        MDC.remove(MDCKey.UID);
    }


    public void renewalTokenIfNecessary(String token) {
        Long userId = JWTUtil.parseJwtUserId(token);
        //根据token查询redis中对应的信息
        String user;
        String key = RedisConstant.USER_LOGIN + userId;
        try {
            user = redisTemplate.opsForValue().get(key);
            if (StrUtil.isBlank(user)) return;
            Long ttl = redisTemplate.getExpire(key, TimeUnit.DAYS);
            if (ttl == null) return;
            //最后一天刷新token的过期时间
            if (ttl < USER_REFRESH_TTL) redisTemplate.expire(key, RedisConstant.USER_LOGIN_TTL, TimeUnit.DAYS);
        } catch (Exception e) {
            log.error("redis异常:[{}]", key, e);
        }
    }


}

