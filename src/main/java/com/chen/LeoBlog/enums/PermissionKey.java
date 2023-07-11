package com.chen.LeoBlog.enums;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public enum PermissionKey {
    USER_VIEW("user:view"),
    USER_CREATE("user:create"),
    USER_UPDATE("user:update"),
    USER_DELETE("user:delete"),
    ARTICLE_VIEW("article:view"),
    ARTICLE_CREATE("article:create"),
    ARTICLE_UPDATE("article:update"),
    ARTICLE_DELETE("article:delete"),
    DRAFT_VIEW("draft:view"),
    DRAFT_CREATE("draft:create"),
    DRAFT_UPDATE("draft:update"),
    DRAFT_DELETE("draft:delete"),
    COMMENT_VIEW("comment:view"),
    COMMENT_CREATE("comment:create"),
    COMMENT_UPDATE("comment:update"),
    COMMENT_DELETE("comment:delete"),
    REPLY_VIEW("reply:view"),
    REPLY_CREATE("reply:create"),
    REPLY_UPDATE("reply:update"),
    REPLY_DELETE("reply:delete"),
    BADGE_VIEW("badge:view"),
    BADGE_BUY("badge:buy"),
    BADGE_ADD("badge:add"),
    BADGE_UPDATE("badge:update"),
    BADGE_DELETE("badge:delete");

    private String actionKey;

}