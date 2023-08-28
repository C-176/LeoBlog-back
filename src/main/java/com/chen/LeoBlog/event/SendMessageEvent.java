package com.chen.LeoBlog.event;

import com.chen.LeoBlog.websocket.vo.WebSocketData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEvent;

@Slf4j
public class SendMessageEvent extends ApplicationEvent {
    public SendMessageEvent(WebSocketData source) {
        super(source);
    }
}
