package com.chen.LeoBlog.aspect;

import cn.hutool.core.util.StrUtil;
import com.chen.LeoBlog.annotation.RedissonLock;
import com.chen.LeoBlog.utils.RedisUtil;
import com.chen.LeoBlog.utils.SpElUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Method;

@Slf4j
@Aspect
@Component
// 优先级最高, 保证在事务之前执行
@Order(0)
public class RedissonLockAspect {
    @Resource
    private RedisUtil redisUtil;

    @Around("@annotation(com.chen.LeoBlog.annotation.RedissonLock)")
    public Object around(ProceedingJoinPoint joinPoint) {
        // 获取切点方法
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        // 获取注解
        RedissonLock redissonLock = method.getAnnotation(RedissonLock.class);
        //计算分布式锁的key：默认方法限定名+注解排名（可能多个）
        String prefix = StrUtil.isBlank(redissonLock.prefixKey()) ? SpElUtils.getMethodKey(method) : redissonLock.prefixKey();
        Object key = SpElUtils.parseSpEl(method, joinPoint.getArgs(), redissonLock.key());
        // 获取锁并执行方法
        return redisUtil.executeWithLock(prefix + ":" + key, redissonLock.waitTime(), redissonLock.unit(), joinPoint::proceed);
    }
}
