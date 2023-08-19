package com.chen.LeoBlog.activityEvent;


import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
public enum ActivityEnum {
    // 文章发布
    ARTICLE_PUBLISH(1, "文章发布"),
    // 创建草稿
    ARTICLE_DRAFT(2, "创建草稿"),
    // 点赞文章
    ARTICLE_LIKE(3, "点赞文章"),
    // 收藏文章
    ARTICLE_COLLECT(4, "收藏文章"),
    // 评论文章
    ARTICLE_COMMENT(5, "评论文章"),
    // 回复评论
    ARTICLE_COMMENT_REPLY(6, "回复评论"),
    // 关注用户
    USER_FOLLOW(7, "关注用户"),
    // 评论活动
    ACTIVITY_COMMENT(8, "评论活动"),
    // 购买商品
    GOODS_BUY(9, "购买商品");

    final Integer activityEventId;

    final String activityEventName;

    private static Map<Integer, ActivityEnum> map;


    static {
        map = Arrays.stream(ActivityEnum.values()).collect(Collectors.toMap(ActivityEnum::getActivityEventId, Function.identity()));
    }

    public static ActivityEnum of(Integer activityEventId) {
        return map.get(activityEventId);
    }

    ActivityEnum(Integer activityEventId, String activityEventName) {
        this.activityEventName = activityEventName;
        this.activityEventId = activityEventId;
    }


}
