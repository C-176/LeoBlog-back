package com.chen.LeoBlog.handler;

import com.chen.LeoBlog.exception.FrequencyControlException;
import com.chen.LeoBlog.publisher.SendMessageEventPublisher;
import com.chen.LeoBlog.websocket.SocketService;
import com.chen.LeoBlog.websocket.vo.WebSocketData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.annotation.Resource;

@RestControllerAdvice
@Slf4j
public class FrequencyControlExceptionHandler {

    @Resource
    private SocketService socketService;
    @Resource
    private SendMessageEventPublisher sendMessageEventPublisher;

    @ExceptionHandler(FrequencyControlException.class)
    public void handleFrequencyControlException(FrequencyControlException e) {
        log.error("频率限制异常", e);
        Long receiveId = e.getReceiveId();
        sendMessageEventPublisher.publish(WebSocketData.frequencyControlNotice(receiveId));
    }
}

