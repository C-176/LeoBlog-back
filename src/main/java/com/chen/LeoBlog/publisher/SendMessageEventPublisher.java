package com.chen.LeoBlog.publisher;

import com.chen.LeoBlog.event.SendMessageEvent;
import com.chen.LeoBlog.websocket.vo.WebSocketData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Slf4j
public class SendMessageEventPublisher {

    @Resource
    private ApplicationEventPublisher applicationEventPublisher;

    public void publish(WebSocketData webSocketData) {
        applicationEventPublisher.publishEvent(new SendMessageEvent(webSocketData));
    }
}
