package com.chen.LeoBlog.base;


import cn.hutool.core.util.RandomUtil;
import com.chen.LeoBlog.constant.RedisConstant;
import com.chen.LeoBlog.utils.AssertUtil;
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

    /**
     * 生成六位验证码，存入redis，返回验证码
     *
     * @param type 邮箱或者手机
     * @return 验证码
     */
    public String send(String type) {
        // 生成6位验证码
        code = RandomUtil.randomString(6);
        redisTemplate.opsForValue().set(RedisConstant.USER_CAPTCHA + type, code, RedisConstant.USER_CAPTCHA_TTL, TimeUnit.MINUTES);
        return code;
    }

    public static String getCode() {
        return code;
    }

    public boolean confirmCode(String code, String type) {
        String savedCaptcha = redisTemplate.opsForValue().get(RedisConstant.USER_CAPTCHA + type);
        AssertUtil.isFalse(savedCaptcha == null, "验证码已过期，请重新获取");
        return savedCaptcha.equals(code);
    }
}
