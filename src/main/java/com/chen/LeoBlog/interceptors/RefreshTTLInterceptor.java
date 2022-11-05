package com.chen.LeoBlog.interceptors;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.chen.LeoBlog.base.Local;
import com.chen.LeoBlog.constant.RedisConstant;
import com.chen.LeoBlog.dto.UserDto;
import com.chen.LeoBlog.po.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;
import java.util.concurrent.TimeUnit;


@Slf4j
public class RefreshTTLInterceptor implements HandlerInterceptor {

    private final StringRedisTemplate  redisTemplate;
    public RefreshTTLInterceptor(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public boolean preHandle(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response, Object handler){
        //从请求头中拿到token
        String token = request.getHeader("Authorization");
        //如果没有传递token，直接放行
        if(token == null || StrUtil.isBlank(token)) return true;
        //根据token查询redis中对应的信息
        String value="";
        Object user;
        String key = RedisConstant.USER_LOGIN + token;
        try{
            user = redisTemplate.opsForHash().get(key, "user");
            if(user != null){
                value = user.toString();
            }else{
                return true;
            }
        }catch (Exception e){
            log.error("redis查询异常:[{}]",key,e);
        }
        if(StrUtil.isBlank(value)) return true;

        //刷新token的过期时间：2DAY
        redisTemplate.expire(key, RedisConstant.USER_LOGIN_TTL, TimeUnit.DAYS);
        user = JSONUtil.toBean(value, User.class);

        UserDto userDto = new UserDto();
        BeanUtil.copyProperties(user, userDto);
        Local.saveUser(userDto);
        if(Local.getUser() == null) {
            log.error("设置Local失败");
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        Local.removeUser();
    }


}

