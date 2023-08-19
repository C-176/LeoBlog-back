package com.chen.LeoBlog.utils;

import cn.hutool.json.JSONUtil;
import com.chen.LeoBlog.constant.RedisConstant;
import com.chen.LeoBlog.lambda.FunctionThrow;
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
     * @param key      redis中的key
     * @param obj      要存入的对象
     * @param ttl      过期时间
     * @param timeUnit 时间单位
     */
    public void saveObjAsJson(String key, Object obj, Long ttl, TimeUnit timeUnit) {
        String jsonStr = JSONUtil.toJsonStr(obj);
        redisTemplate.opsForValue().set(key, jsonStr, ttl, timeUnit);
    }

    /**
     * 从redis中取出json字符串并转为对象
     *
     * @param key   redis中的key
     * @param clazz 要反序列化的class
     * @param <T>   要反序列化的类型
     * @return T
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
     * @param key      redis中的key
     * @param id       查询数据的索引字段
     * @param clazz    要反序列化的class
     * @param ttl      redis中的过期时间
     * @param timeUnit 时间单位
     * @param function 从数据库中取数据的方法
     * @param <ID>     索引字段的类型
     * @param <T>      要反序列化的class
     * @return T
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

    /**
     * 获取分布式锁
     *
     * @param lockKey 锁的key，默认10秒超时
     * @return 锁对象，如果获取失败返回null
     * @throws InterruptedException
     */
    public RLock getLock(String lockKey) throws InterruptedException {
        RLock lock = redissonClient.getLock(lockKey);
        boolean isSuccess = lock.tryLock(1L, 10L, TimeUnit.SECONDS);
        return isSuccess ? lock : null;
    }

    /**
     * 获取分布式锁
     *
     * @param lockKey  锁的key
     * @param waitTime 等待时间
     * @param timeUnit 时间单位
     * @return 锁对象，如果获取失败返回null
     * @throws InterruptedException
     */
    public RLock getLock(String lockKey, long waitTime, TimeUnit timeUnit) throws InterruptedException {
        RLock lock = redissonClient.getLock(lockKey);
        boolean isSuccess = lock.tryLock(waitTime, 10L, timeUnit);
        return isSuccess ? lock : null;
    }

    public void releaseLock(RLock lock) {
        lock.unlock();
    }

    /**
     * 在执行方法前获取对应key的分布式锁
     *
     * @param key      锁的key
     * @param waitTime 等待时间，-1表示一直等待
     * @param timeUnit 时间单位
     * @param supplier 执行方法，方法引用
     * @param <T>      方法返回值类型
     * @return 方法执行结果
     */
    public <T> T executeWithLock(String key, int waitTime, TimeUnit timeUnit, FunctionThrow<?> supplier) {
        RLock lock = null;
        try {
            if (waitTime == -1) lock = getLock(key);
            else lock = getLock(key, waitTime, timeUnit);
            if (lock == null) {
                throw new RuntimeException("获取锁失败");
            }
            return (T) supplier.apply();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        } finally {
            if (lock != null) releaseLock(lock);
        }
    }


    public Long incr(String key) {
        return redisTemplate.opsForValue().increment(key);
    }

    public Long incr(String key, int step) {
        return redisTemplate.opsForValue().increment(key, step);
    }

}
