package com.chen.LeoBlog.handler;

import com.chen.LeoBlog.exception.FrequencyControlException;
import com.chen.LeoBlog.websocket.SocketService;
import com.chen.LeoBlog.websocket.vo.WebSocketData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.annotation.Resource;
import javax.websocket.Session;

@RestControllerAdvice
@Slf4j
public class FrequencyControlExceptionHandler {

    @Resource
    private SocketService socketService;

    @ExceptionHandler(FrequencyControlException.class)
    public void handleFrequencyControlException(FrequencyControlException e) {
        log.error("频率限制异常", e);
        Session session = e.getSession();
        if (session != null) {
            socketService.sendToSession(session, WebSocketData.frequencyControlNotice());
        }
    }
}
