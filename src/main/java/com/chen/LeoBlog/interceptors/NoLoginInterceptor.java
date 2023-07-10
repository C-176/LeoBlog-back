package com.chen.LeoBlog.interceptors;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.chen.LeoBlog.annotation.Anonymous;
import com.chen.LeoBlog.base.Local;
import com.chen.LeoBlog.constant.RedisConstant;
import com.chen.LeoBlog.dto.UserDTO;
import com.chen.LeoBlog.po.LoginUser;
import com.chen.LeoBlog.utils.JWTUtil;
import com.chen.LeoBlog.utils.WebUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@Slf4j
public class NoLoginInterceptor implements HandlerInterceptor {
    private final StringRedisTemplate redisTemplate;

    public NoLoginInterceptor(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if ("OPTIONS".equals(request.getMethod()) || "/error".equals(request.getRequestURI())) {
            return true;
        }
        //, "/user/{id:[-0-9]+}"
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            // 如果调用该handler的接口上有Anonymous注解，则不需要校验token，直接放行
            boolean isSkipInterception = handlerMethod.getMethod().isAnnotationPresent(Anonymous.class);
            if (isSkipInterception) return true;
        }

        //从请求头中拿到token
        String token = request.getHeader("Authorization");

        //如果请求体中没有token，说明没有登陆过。（要求登陆必须带着token）
        if (token == null || StrUtil.isBlank(token)) {
            log.error(request.getRequestURI());
            log.debug("token为空:{}", token);
            WebUtil.responseMsg(response, 401, "用户未登录");
            return false;
        }

        String userId = String.valueOf(JWTUtil.parseJwtUserId(token));
        //根据token去redis中查询对应的用户信息
        Object s = redisTemplate.opsForValue().get(RedisConstant.USER_LOGIN + userId);
        //如果查不到信息，或者为空，说明用户登陆信息，已经过期，需要重新登陆。
        if (s == null) {
            log.error("redis中没有用户信息:[{}]", (RedisConstant.USER_LOGIN + userId));
            WebUtil.responseMsg(response, HttpStatus.UNAUTHORIZED.value(), "用户未登录");
            return false;
        }

        LoginUser user = JSONUtil.toBean(s.toString(), LoginUser.class);


        UserDTO userDto = new UserDTO();
        BeanUtil.copyProperties(user.getUser(), userDto);
        try {
            Local.saveUser(userDto);
        } catch (Exception e) {
            log.error("Local.saveUser(userDto)出错");
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        Local.removeUser();
    }


}


