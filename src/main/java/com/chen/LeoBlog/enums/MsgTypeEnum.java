package com.chen.LeoBlog.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Arrays;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public enum MsgTypeEnum {
    //    :0-发表文章 1-评论文章 2-收藏文章 3-点赞文章
//     * 4-关注用户 5-回复评论 6-系统消息
    PUBLISH_ARTICLE(0, "发表文章"),
    COMMENT_ARTICLE(1, "评论文章"),
    COLLECT_ARTICLE(2, "收藏文章"),
    LIKE_ARTICLE(3, "点赞文章"),
    FOLLOW_USER(4, "关注用户"),
    REPLY_COMMENT(5, "回复评论"),
    SYSTEM_MESSAGE(6, "系统消息");

    private Integer code;
    private String info;

    public static MsgTypeEnum of(Integer code) {
        return Arrays.stream(MsgTypeEnum.values())
                .filter(x -> x.getCode().equals(code)).findFirst().orElse(null);
    }
}
