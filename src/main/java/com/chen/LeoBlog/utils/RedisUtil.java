package com.chen.LeoBlog.utils;

import cn.hutool.json.JSONUtil;
import com.chen.LeoBlog.constant.RedisConstant;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

@Slf4j
@Component
public class RedisUtil {
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Resource
    private RedissonClient redissonClient;


    /**
     * 将对象转为json字符串存入redis
     *
     * @param key
     * @param obj
     * @param ttl
     * @param timeUnit
     */
    public void saveObjAsJson(String key, Object obj, Long ttl, TimeUnit timeUnit) {
        String jsonStr = JSONUtil.toJsonStr(obj);
        redisTemplate.opsForValue().set(key, jsonStr, ttl, timeUnit);
    }

    /**
     * 从redis中取出json字符串并转为对象
     *
     * @param key
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> T getObj(String key, Class<T> clazz) {
        String jsonStr = redisTemplate.opsForValue().get(key);
        if (jsonStr == null)
            return null;
        return JSONUtil.toBean(jsonStr, clazz);
    }

    /**
     * 从redis中取出对象，如果没有就从数据库中取
     *
     * @param key
     * @param id
     * @param clazz
     * @param ttl
     * @param timeUnit
     * @param function
     * @param <ID>
     * @param <T>
     * @return
     */
    public <ID, T> T getObjWithCache(String key, ID id, Class<T> clazz, Long ttl, TimeUnit timeUnit, Function<ID, T> function) {
        T t = getObj(key, clazz);
        if (t != null) return t;
        String lockKey = RedisConstant.LOCK_PREFIX + key;

        RLock lock;
        try {
            lock = getLock(lockKey);
        } catch (InterruptedException e) {
            log.error("redis锁异常", e);
            throw new RuntimeException(e);
        }
        if (lock != null) {
            try {
                //如果redis中没有，就从数据库中取
                t = getObj(key, clazz);
                if (t != null) return t;
                t = function.apply(id);
                if (t == null) return null;
                saveObjAsJson(key, t, ttl, timeUnit);
            } catch (Exception e) {
                log.error("redis缓存异常", e);
            } finally {
                releaseLock(lock);
            }
        } else {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return getObjWithCache(key, id, clazz, ttl, timeUnit, function);
        }

        return t;

    }

    public RLock getLock(String lockKey) throws InterruptedException {
        RLock lock = redissonClient.getLock(lockKey);
        boolean isSuccess = lock.tryLock(1L, 10L, TimeUnit.SECONDS);
        return isSuccess ? lock : null;
    }

    public void releaseLock(RLock lock) {
        lock.unlock();
    }
}
