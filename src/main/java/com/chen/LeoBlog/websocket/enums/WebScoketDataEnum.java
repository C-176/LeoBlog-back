package com.chen.LeoBlog.websocket.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum WebScoketDataEnum {

    // 聊天相关
    // 单聊
    SINGLE_CHAT(101, "单聊消息"),
    // 群聊
    GROUP_CHAT(102, "群聊消息"),
    // AI聊天
    AI_CHAT(103, "AI聊天"),
    // 上线通知
    ONLINE_NOTICE(104, "上线通知"),
    // 下线通知
    OFFLINE_NOTICE(105, "下线通知"),


    // 系统通知
    SYSTEM_NOTICE(201, "系统通知"),

    // 新增活动事件
    NEW_ACTIVITY_NOTICE(202, "新增活动事件"),
    FREQUENCY_CONTROL_NOTICE(203, "限流通知"),

    // websocket连接相关
    // 心跳
    HEART_BEAT(301, "心跳"),
    // 心跳响应
    HEART_BEAT_RESPONSE(302, "心跳响应"),
    // 连接请求
    CONNECT_REQUEST(303, "连接请求"),
    // 连接响应
    CONNECT_RESPONSE(304, "连接成功响应"),

    // 认证失败响应
    AUTH_FAIL_RESPONSE(306, "认证失败响应"),
    // 认证成功响应
    AUTH_SUCCESS_RESPONSE(307, "认证成功响应"),
    // 强制下线响应
    FORCE_OFFLINE_RESPONSE(308, "强制下线响应"),
    // accessToken刷新响应
    REFRESH_ACCESSION(309, "accessToken刷新响应"),


    ;

    Integer type;
    String desc;
    private static final Map<Integer, WebScoketDataEnum> map;

    static {
        map = Arrays.stream(WebScoketDataEnum.values()).collect(Collectors.toMap(WebScoketDataEnum::getType, Function.identity()));
    }

    public static WebScoketDataEnum of(Integer type) {
        return map.get(type);
    }


}
