package com.chen.LeoBlog.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static com.chen.LeoBlog.constant.RedisConstant.ICR_ID;

@Component
@Slf4j
public class IdUtil {
    @Autowired
    private StringRedisTemplate redisTemplate;

    public static final Long BEGIN_TIMESTAMP = 1665757184L;
    public static final int COUNT_BITS = 10;

    public Long nextId(String key) {
//        获取当前时间戳
        LocalDateTime now = LocalDateTime.now();
        long nowSecond = now.toEpochSecond(ZoneOffset.UTC);
        long timestamp = nowSecond - BEGIN_TIMESTAMP;

        long aLong = redisTemplate.opsForValue().increment(ICR_ID + key, 1);
        log.info("自增id" + aLong);
        return timestamp << COUNT_BITS | aLong;

    }
}
