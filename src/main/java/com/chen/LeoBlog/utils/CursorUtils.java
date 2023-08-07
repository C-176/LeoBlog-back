package com.chen.LeoBlog.utils;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Pair;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.chen.LeoBlog.vo.request.CursorPageBaseReq;
import com.chen.LeoBlog.vo.response.CursorPageBaseResp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Description: 游标分页工具类
 * Author: <a href="https://github.com/zongzibinbin">abin</a>
 * Date: 2023-03-28
 */
@Component
public class CursorUtils {
    @Autowired
    private StringRedisTemplate redisTemplate;

    public <T> CursorPageBaseResp<Pair<T, Double>> getCursorPageByRedis(CursorPageBaseReq cursorPageBaseReq, String redisKey, Function<String, T> typeConvert) {
        Set<ZSetOperations.TypedTuple<String>> typedTuples;
        //第一次访问就直接返回最新的数据，以后都是根据传入的cursor为上限进行查询
        if (StrUtil.isBlank(cursorPageBaseReq.getCursor())) {
            typedTuples = RedisUtils.zReverseRangeWithScores(redisKey, cursorPageBaseReq.getPageSize());
        } else {
            typedTuples = RedisUtils.zReverseRangeByScoreWithScores(redisKey, Double.parseDouble(cursorPageBaseReq.getCursor()), cursorPageBaseReq.getPageSize());
        }
//        (v,s)
        List<Pair<T, Double>> result = typedTuples.stream().map(t -> Pair.of(typeConvert.apply(t.getValue()), t.getScore())).sorted((o1, o2) -> o2.getValue().compareTo(o1.getValue())) // 根据值倒序排列
                .toList();
        String cursor = Optional.ofNullable(CollectionUtil.getLast(result)).map(Pair::getValue).map(String::valueOf).orElse(null);
        return new CursorPageBaseResp(cursor, result.size() != cursorPageBaseReq.getPageSize(), 0, result);
    }

    public <T> CursorPageBaseResp<T> getCursorPageByMysql(
            IService<T> mapper, CursorPageBaseReq request,
            LambdaQueryChainWrapper<T> wrapper, SFunction<T, ?> cursorColumn) {

        // 如果不是第一次查询，那么cursor不为空，需要加上小于条件
        if (StrUtil.isNotBlank(request.getCursor())) {
            wrapper.lt(cursorColumn, request.getCursor());
        }
        // 按照cursorColumn降序排列
        wrapper.orderByDesc(cursorColumn);
        Page<T> page = mapper.page(request.plusPage(), wrapper.getWrapper());
        String cursor = Optional.ofNullable(CollectionUtil.getLast(page.getRecords())).map(cursorColumn).map(String::valueOf).orElse(null);
        Collections.reverse(page.getRecords());
        Boolean isLast = page.getRecords().size() != request.getPageSize();
        return new CursorPageBaseResp<>(cursor, isLast, isLast ? 0 : 0, page.getRecords());
    }

    /**
     * 获取游标分页
     *
     * @param key           redis key
     * @param req           请求参数
     * @param wrapper       查询条件
     * @param functionThrow 转换器
     * @param primaryKey    redis中存放的主键
     * @param <T>           实体类型
     * @return 游标分页
     */

    public <T> CursorPageBaseResp<T> getCursorPage(String key, CursorPageBaseReq req,
                                                   QueryChainWrapper<T> wrapper, Function<T, ?> functionThrow, String primaryKey) {
        Integer offset = req.getOffset();
        Integer count = req.getPageSize();
        // 获取游标对应的分数
        // 取出消息
        Set<ZSetOperations.TypedTuple<String>> typedTuples;
        if (StrUtil.isNotBlank(req.getCursor())) typedTuples = redisTemplate.opsForZSet()
                // 最大的分数在后面，并且往上翻，找更小的分数
                .reverseRangeByScoreWithScores(key, 0, Long.parseLong(req.getCursor()), offset, count);
            // 最小的分数在前面，并且往上翻，找更大的分数
            // .rangeByScoreWithScores(key, lastScore, 0, offset, count);
        else
            typedTuples = redisTemplate.opsForZSet().reverseRangeByScoreWithScores(key, 0, Long.MAX_VALUE, offset, count);

        if (typedTuples == null || typedTuples.isEmpty()) {
            return CursorPageBaseResp.empty();
        }
        List<String> values = typedTuples.stream().map(ZSetOperations.TypedTuple::getValue).filter(Objects::nonNull).toList();
        List<Long> scores = typedTuples.stream().map(ZSetOperations.TypedTuple::getScore).map(Double::longValue).toList();

        // 计算偏移量
        Long cursor = RandomUtil.randomLong();
        offset = 1;
        for (Long score : scores) {
            // 计算当前查到数据中最后一个分数的重复个数
            if (cursor.equals(score)) offset++;
            else { // 如果不相等，说明已经到了下一个分数的数据，重置偏移量
                cursor = score;
                offset = 1;
            }
        }
        // 查询，并且保证顺序
        // 转化为Long
        String ids = StrUtil.join(",", values);
        List<T> result = wrapper.in(primaryKey, values).last("order by field(" + primaryKey + "," + ids + ")").list();
        Set<String> existIds = result.stream().map(functionThrow).map(String::valueOf).collect(Collectors.toSet());
        deleteNoExist(key, values, existIds);
        return CursorPageBaseResp.of(String.valueOf(cursor), offset, result, req.getPageSize());
    }

    @Async
    protected void deleteNoExist(String key, List<String> values, Set<String> existIds) {
        // 如果查询出来的文章id和redis中的id不一致，说明有文章被删除了，需要删除redis中的数据
        int originalSize = values.size();
        if (existIds.size() != originalSize) {
            values.forEach(id -> {
                if (!existIds.contains(id)) redisTemplate.opsForZSet().remove(key, id);
            });
        }
    }

}
