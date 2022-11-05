package com.chen.LeoBlog.base;

import cn.hutool.core.util.RandomUtil;
import com.chen.LeoBlog.constant.RedisConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class CodeSender {
    private static String code;

    @Autowired
    private StringRedisTemplate redisTemplate;

    public String send(String type) {
        // 生成6位验证码
        code = RandomUtil.randomString(6);
        redisTemplate.opsForValue().set(RedisConstant.USER_CAPTCHA + type,code, RedisConstant.USER_CAPTCHA_TTL, TimeUnit.MINUTES);
        return code;
    }
    public static String getCode() {
        return code;
    }
    public static boolean confirmCode(String code1) {
        return code.equals(code1);
    }
}
