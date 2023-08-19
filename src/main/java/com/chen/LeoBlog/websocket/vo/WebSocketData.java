package com.chen.LeoBlog.websocket.vo;

import com.chen.LeoBlog.websocket.enums.WebScoketDataEnum;
import lombok.*;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class WebSocketData {
    public Long receiveId;
    public Integer type;
    public String content;
    public Date sendTime;

    // 心跳包回复
    public static WebSocketData heartBeatResponse() {
        return WebSocketData.builder()
                .type(WebScoketDataEnum.HEART_BEAT_RESPONSE.getType())
                .content("pong")
                .sendTime(new Date())
                .build();
    }

    // 上线通知
    public static WebSocketData onlineNotice(Long userId) {
        return WebSocketData.builder()
                .type(WebScoketDataEnum.ONLINE_NOTICE.getType())
                .content(userId + "")
                .sendTime(new Date())
                .build();
    }

    // 下线通知
    public static WebSocketData offlineNotice(Long userId) {
        return WebSocketData.builder()
                .type(WebScoketDataEnum.OFFLINE_NOTICE.getType())
                .content(userId + "")
                .sendTime(new Date())
                .build();
    }

    // 验证失败
    public static WebSocketData authFailResponse() {
        return WebSocketData.builder()
                .type(WebScoketDataEnum.AUTH_FAIL_RESPONSE.getType())
                .content("token非法")
                .sendTime(new Date())
                .build();
    }

    // 强制下线
    public static WebSocketData forceOfflineResponse() {
        return WebSocketData.builder()
                .type(WebScoketDataEnum.FORCE_OFFLINE_RESPONSE.getType())
                .content("连接不活跃，强制下线")
                .sendTime(new Date())
                .build();
    }

    // 单聊消息
    public static WebSocketData singleChatData(Long receiveId, String content) {
        return WebSocketData.builder()
                .type(WebScoketDataEnum.SINGLE_CHAT.getType())
                .receiveId(receiveId)
                .content(content)
                .sendTime(new Date())
                .build();
    }

    // 系统通知
    public static WebSocketData systemNotice(String content) {
        return WebSocketData.builder()
                .type(WebScoketDataEnum.SYSTEM_NOTICE.getType())
                .content(content)
                .sendTime(new Date())
                .build();
    }

    // 群组消息
    public static WebSocketData groupChatData(Long receiveId, String content) {
        return WebSocketData.builder()
                .type(WebScoketDataEnum.GROUP_CHAT.getType())
                .receiveId(receiveId)
                .content(content)
                .sendTime(new Date())
                .build();
    }

    // 活动事件通知
    public static WebSocketData newActivityNotice(String content) {
        return WebSocketData.builder()
                .type(WebScoketDataEnum.NEW_ACTIVITY_NOTICE.getType())
                .content(content)
                .sendTime(new Date())
                .build();
    }

    public static WebSocketData accessToken(String accessToken) {
        return WebSocketData.builder()
                .type(WebScoketDataEnum.REFRESH_ACCESSION.getType())
                .content(accessToken)
                .sendTime(new Date())
                .build();
    }

    public static WebSocketData frequencyControlNotice() {
        return WebSocketData.builder()
                .type(WebScoketDataEnum.FREQUENCY_CONTROL_NOTICE.getType())
                .content("请求过于频繁或发消息过于频繁")
                .sendTime(new Date())
                .build();
    }
}
