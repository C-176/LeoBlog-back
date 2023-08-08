package com.chen.LeoBlog.utils;

import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static com.chen.LeoBlog.constant.RedisConstant.ICR_ID;

//@Component
//@Slf4j
//public class IdUtil {
//
//
//
//
//    public Long nextId(String key) {
//////        获取当前时间戳
////        LocalDateTime now = LocalDateTime.now();
////        long nowSecond = now.toEpochSecond(ZoneOffset.UTC);
////        long timestamp = nowSecond - BEGIN_TIMESTAMP;
////        //todo:分布式id生成器的实现
////        long id = redisUtil.incr(ICR_ID + key, 1);
////        log.info("自增id" + id);
////        return timestamp << COUNT_BITS | id;
//
//    }
//}

@Component
@Slf4j
public class IdUtil implements IdentifierGenerator {
    @Resource
    private RedisUtil redisUtil;
    public static final Long BEGIN_TIMESTAMP = 1665757184L;
    public static final int COUNT_BITS = 10;

    @Override
    public Long nextId(Object entity) {

        //可以将当前传入的class全类名来作为bizKey,或者提取参数来生成bizKey进行分布式Id调用生成.
        String key = entity.getClass().getTypeName().toLowerCase();
        if (entity instanceof Class<?>) {
            key = ((Class<?>) entity).getTypeName().toLowerCase();
        }
        log.info("key:" + key);
        //        获取当前时间戳
        LocalDateTime now = LocalDateTime.now();
        long nowSecond = now.toEpochSecond(ZoneOffset.UTC);
        long timestamp = nowSecond - BEGIN_TIMESTAMP;
        //todo:分布式id生成器的实现
        long id = redisUtil.incr(ICR_ID + key, 1);
        log.info("自增id" + id);
        return timestamp << COUNT_BITS | id;
    }
}
