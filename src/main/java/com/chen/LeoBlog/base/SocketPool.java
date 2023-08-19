package com.chen.LeoBlog.base;

import javax.websocket.Session;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WebSocket连接池类
 */
public class SocketPool {

    // 在线用户websocket连接池
    private static final ConcurrentHashMap<Long, Session> ONLINE_USER_SESSIONS = new ConcurrentHashMap<>();

    /**
     * 新增一则连接
     *
     * @param keyId   设置主键
     * @param session 设置session
     */
    public static void add(Long keyId, Session session) {

        if (keyId != null && session != null) {
            //生成一个唯一的keyId
//            UUID uuid = UUID.randomUUID(true);
            ONLINE_USER_SESSIONS.put(keyId, session);
        }
    }

    /**
     * 根据Key删除连接
     *
     * @param keyId 主键
     */
    public static void remove(Long keyId) {
        if (keyId != null) {
            ONLINE_USER_SESSIONS.remove(keyId);
        }
    }

    /**
     * 获取在线人数
     *
     * @return 返回在线人数
     */
    public static int count() {
        return ONLINE_USER_SESSIONS.size();
    }

    /**
     * 获取在线session池
     *
     * @return 获取session池
     */
    public static Map<Long, Session> getSessionMap() {
        return ONLINE_USER_SESSIONS;
    }
}
