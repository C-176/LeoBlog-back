package com.chen.LeoBlog.constant;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class RedisConstant {
    //登陆后用户信息
    public static final String USER_LOGIN = "user:login:";
    //刷新token登陆时间
    public static final Long USER_LOGIN_TTL = 7L;
    //验证码
    public static final String USER_CAPTCHA = "user:captcha:";
    //验证码过期时间
    public static final Long USER_CAPTCHA_TTL = 5L;
    //自增id
    public static final String ICR_ID = "icr:id:";
    //缓存用户信息
    public static final String USER_INFO = "user:info:";
    public static final long USER_INFO_TTL = 1L;

    //缓存文章点赞信息
    public static final String ARTICLE_LIKE = "article:like:";
    //缓存文章收藏信息
    public static final String ARTICLE_COLLECT = "article:collect:";

    //聊天对象
    public static final String CHAT_USER_LIST = "chat:user:";

    public static final String CHAT_ALL_LIST = "chat:all";

}
