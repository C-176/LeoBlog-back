package com.chen.LeoBlog.constant;

public class RedisConstant {
    // 通用过期时间
    public static final Long TTL = 7L;
    //登陆后用户信息
    public static final String USER_LOGIN = "user:login:";
    //token过期时间
    public static final Long USER_LOGIN_TTL = 7L;
    // 刷新token的剩余时间
    public static final Long USER_REFRESH_TTL = 1L;

    //验证码
    public static final String USER_CAPTCHA = "user:captcha:";
    //验证码过期时间
    public static final Long USER_CAPTCHA_TTL = 5L;
    //自增id
    public static final String ICR_ID = "icr:id:";
    //缓存用户信息
    public static final String USER_INFO = "user:info:";
    public static final long USER_INFO_TTL = 7L;

    // 文章信息
    public static final String ARTICLE_INFO = "article:info:";
    public static final long ARTICLE_INFO_TTL = 7L;

    //缓存文章点赞信息
    public static final String ARTICLE_LIKE = "article:like:";
    //缓存文章收藏信息
    public static final String ARTICLE_COLLECT = "article:collect:";

    //聊天好友
    public static final String CHAT_FRIEND_LIST = "online:friend:";

    public static final String ONLINE_ALL = "online:all";
    // 锁的过期时间
    public static final Long LOCK_EXPIRE_TIME = 10L;
    // 锁的key
    public static final String LOCK_PREFIX = "lock:";

    // 徽章信息
    public static final String BADGE_INFO = "badge:info:";
    public static final long BADGE_INFO_TTL = 7L;

    // 用户ID锁
    public static final String USER_ID_LOCK = "lock:user_id:";

    // badge持有者
    public static final String BADGE_OWNER = "badge:owner:";
    public static final long BADGE_OWNER_TTL = 7L;

    // 用户账户信息
    public static final String ACCOUNT_INFO = "account:info:";
    public static final long ACCOUNT_INFO_TTL = 7L;

    //徽章库存
    public static final String BADGE_STOCK = "badge:stock:";
    public static final long BADGE_STOCK_TTL = 7L;

    //关注列表：我关注了谁
    public static final String FOLLOW_USER_LIST = "follow:user:";

    //被关注列表:有哪些人关注了我
    public static final String FAN_USER_LIST = "fan:user:";

    // 收件箱
    public static final String MESSAGE_BOX_PREFIX = "message:";


    public static final String ACTIVITY_USER = "activity:user:";
    public static final String CHAT_GROUP_LIST = "chat:group:list:";
}
