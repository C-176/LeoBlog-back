package com.chen.LeoBlog.websocket;


import com.chen.LeoBlog.base.SocketPool;
import com.chen.LeoBlog.exception.CommonErrorEnum;
import com.chen.LeoBlog.utils.AssertUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.websocket.RemoteEndpoint;
import javax.websocket.Session;
import java.io.IOException;


@Service
@Slf4j
public class SocketService {

    /**
     * 给指定用户发送信息
     *
     * @param session session
     * @param msg     发送的消息
     */
    public void sendMessage(Session session, String msg) {
        RemoteEndpoint.Basic basic = session.getBasicRemote();
        try {
            assert basic != null;
            basic.sendText(msg);
        } catch (IOException e) {
            log.error("消息发送异常，异常情况: {}", e.getMessage());
            AssertUtil.isFalse(false, CommonErrorEnum.SYSTEM_ERROR);
        }
    }

    public void sendMessageAll(String message, Long userId) {
        log.info("广播：群发消息");
        // 遍历map
        SocketPool.getSessionMap().forEach((keyId, session) -> {
            if (!userId.equals(keyId)) {
                sendMessage(session, message);
            }
        });
    }
}