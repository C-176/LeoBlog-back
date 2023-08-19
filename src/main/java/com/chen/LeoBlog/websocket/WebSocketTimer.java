package com.chen.LeoBlog.websocket;

import com.chen.LeoBlog.base.SocketPool;
import com.chen.LeoBlog.websocket.vo.WebSocketData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.websocket.Session;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

@Component
@Slf4j
public class WebSocketTimer {
    @Resource
    private SocketService socketService;
    private static final long HEARTBEAT_TIMEOUT = 2 * 60_000; // 心跳超时时间，单位为毫秒
    private static final long HEARTBEAT_CHECK_INTERVAL = 60_000; // 心跳检查间隔，单位为毫秒
    // 用于存放客户端心跳记录的Map，使用ConcurrentSkipListMap保证线程安全，按照value 从大到小排序
    private final Map<Long, Long> clientHeartbeats = new ConcurrentSkipListMap<>();


    // 间隔检测状态池中的连接是否超时
    @Scheduled(fixedRate = HEARTBEAT_CHECK_INTERVAL)
    public void maintainHeartbeatRecords() {
        try {
            kickTimeoutClients();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateHeartbeatRecord(Long clientId) {
        // 更新对应客户端的心跳记录
        long currentTime = System.currentTimeMillis();
        clientHeartbeats.put(clientId, currentTime);
    }

    private void kickTimeoutClients() throws IOException {
        long currentTime = System.currentTimeMillis();
        // 遍历客户端心跳记录，检查是否超时
        Map<Long, Session> sessionMap = SocketPool.getSessionMap();
//        sessionMap.forEach((k, v) -> log.info("key: " + k + " value: " + v));
        for (Long clientId : clientHeartbeats.keySet()) {
            long lastHeartbeat = clientHeartbeats.get(clientId);
            if (currentTime - lastHeartbeat < HEARTBEAT_TIMEOUT) continue;

            // 执行踢出操作，例如断开与客户端的连接
            // 通知客户端被踢出
            Session session = sessionMap.get(clientId);
            if (session != null) {
                socketService.sendToSession(session, WebSocketData.forceOfflineResponse());
                SocketPool.remove(clientId);
                session.close();
            }
            // 移除客户端的心跳记录
            clientHeartbeats.remove(clientId);

        }
    }
}

