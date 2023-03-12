package com.chen.LeoBlog.interceptors;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.chen.LeoBlog.base.Local;
import com.chen.LeoBlog.dto.UserDto;
import com.chen.LeoBlog.exception.NoLoginException;
import com.chen.LeoBlog.constant.RedisConstant;
import com.chen.LeoBlog.po.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@Slf4j
public class NoLoginInterceptor implements HandlerInterceptor {
    private final StringRedisTemplate stringRedisTemplate;

    public NoLoginInterceptor(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }


    @Override
    public synchronized boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //从请求头中拿到token
        String token = request.getHeader("Authorization");

        //如果请求体中没有token，说明没有登陆过。（要求登陆必须带着token）
        if (token == null || StrUtil.isBlank(token)) {
//            log.info("token为空");
            throw new NoLoginException();
        }
        //根据token去redis中查询对应的用户信息
        Object s = stringRedisTemplate.opsForHash().get(RedisConstant.USER_LOGIN + token, "user");
        //如果查不到信息，或者为空，说明用户登陆信息，已经过期，需要重新登陆。
        if (s == null) {
            log.error("redis中没有用户信息:[{}]", (RedisConstant.USER_LOGIN + token));
            throw new NoLoginException();
        }
        User user = JSONUtil.toBean(s.toString(), User.class);

        if (Local.getUser() == null || !(Local.getUser().getUserId().equals(user.getUserId()))) {
            if (Local.getUser() != null) {
                log.info("Local中的用户信息[{}]", Local.getUser().getUserNickname());
            }
            UserDto userDto = new UserDto();
            BeanUtil.copyProperties(user, userDto);
            Local.saveUser(userDto);
            if (Local.getUser() == null) {
                log.error("设置Local失败");
            }
            log.info("将登陆用户信息设置到Local成功[{}]", Local.getUser().getUserNickname());

        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        Local.removeUser();
    }


}


