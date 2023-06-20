package com.chen.LeoBlog.base;

public enum MsgType {
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
    private String msg;

    MsgType(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static MsgType getMsgTypeByCode(Integer code) {
        for (MsgType msgType : MsgType.values()) {
            if (msgType.getCode().equals(code)) {
                return msgType;
            }
        }
        return null;
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

}
