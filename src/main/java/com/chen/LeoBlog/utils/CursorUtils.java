package com.chen.LeoBlog.utils;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Pair;
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

    /**
     * @param cursorPageBaseReq
     * @param redisKey
     * @param typeConvert
     * @param desc              是否降序排序，true为降序，false为升序。比如：排行榜要降序排列，消息记录要正序排列
     * @param <T>
     * @return
     */
    public <T> CursorPageBaseResp<?> getCursorPageByRedis(
            CursorPageBaseReq cursorPageBaseReq, String redisKey, Function<String, T> typeConvert, boolean desc) {
        Set<ZSetOperations.TypedTuple<String>> typedTuples;
        //第一次访问就直接返回最新的数据，以后都是根据传入的cursor为上限进行查询
        if (StrUtil.isBlank(cursorPageBaseReq.getCursor())) {
            typedTuples = RedisUtils.zReverseRangeWithScores(redisKey, cursorPageBaseReq.getPageSize());
        } else {
            typedTuples = RedisUtils.zReverseRangeByScoreWithScores(redisKey, Double.parseDouble(cursorPageBaseReq.getCursor()), cursorPageBaseReq.getPageSize());
        }

        List<Pair<T, Double>> result = typedTuples.stream()
                .map(t -> Pair.of(typeConvert.apply(t.getValue()), t.getScore()))
                .sorted((o1, o2) -> o2.getValue().compareTo(o1.getValue())) // 根据值倒序排列
                .toList();

        List<T> list = new ArrayList<>(result.stream().map(Pair::getKey).toList());
        String cursor = Optional.ofNullable(CollectionUtil.getLast(result)).map(Pair::getValue).map(String::valueOf).orElse(null);
        if (!desc) {
            Collections.reverse(list);
        }
        return CursorPageBaseResp.of(cursor, 1, list, cursorPageBaseReq.getPageSize());
    }


    public <T> CursorPageBaseResp<T> getCursorPageByMysql(
            IService<T> mapper, CursorPageBaseReq request,
            LambdaQueryChainWrapper<T> wrapper, SFunction<T, ?> cursorColumn, boolean desc) {

        // 如果不是第一次查询，那么cursor不为空，需要加上小于条件
        if (StrUtil.isNotBlank(request.getCursor())) {
            wrapper.lt(cursorColumn, request.getCursor());
        }
        // 按照cursorColumn降序排列
        wrapper.orderByDesc(cursorColumn);
        Page<T> page = mapper.page(request.plusPage(), wrapper.getWrapper());
        String cursor = Optional.ofNullable(CollectionUtil.getLast(page.getRecords())).map(cursorColumn).map(String::valueOf).orElse(null);
        if (!desc) Collections.reverse(page.getRecords());
        Boolean isLast = page.getRecords().size() != request.getPageSize();
        return new CursorPageBaseResp<>(cursor, isLast, 1, page.getRecords());
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
                .reverseRangeByScoreWithScores(key, 0, Long.parseLong(req.getCursor()), offset, count);
        else
            typedTuples = redisTemplate.opsForZSet().reverseRangeByScoreWithScores(key, 0, Long.MAX_VALUE, offset, count);

        if (typedTuples == null || typedTuples.isEmpty()) {
            return CursorPageBaseResp.empty();
        }
        // 根据分数排序，倒序
        List<ZSetOperations.TypedTuple<String>> list = typedTuples.stream().sorted(((o1, o2) -> o2.getScore().compareTo(o1.getScore()))).toList();
        List<String> values = list.stream().map(ZSetOperations.TypedTuple::getValue).filter(Objects::nonNull).toList();
        List<Long> scores = list.stream().map(ZSetOperations.TypedTuple::getScore).filter(Objects::nonNull).map(Double::longValue).toList();

        // 计算偏移量
        Long cursor = scores.get(scores.size() - 1);
        offset = 1;
        for (Long score : scores) {
            if (score.equals(cursor)) offset++;
            else break;
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
