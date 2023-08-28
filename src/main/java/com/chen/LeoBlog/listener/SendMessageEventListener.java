package com.chen.LeoBlog.listener;

import cn.hutool.json.JSONUtil;
import com.chen.LeoBlog.event.SendMessageEvent;
import com.chen.LeoBlog.service.KafkaService;
import com.chen.LeoBlog.websocket.vo.WebSocketData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Slf4j
public class SendMessageEventListener {
    public static final String MESSAGE_TOPIC = "message";

    @Resource
    private KafkaService kafkaService;

    @EventListener
    public void onApplicationEvent(SendMessageEvent event) {
        WebSocketData source = (WebSocketData) event.getSource();
        // 发布到对应消息队列
        kafkaService.sendMessage(MESSAGE_TOPIC, JSONUtil.toJsonStr(source));
    }
}
