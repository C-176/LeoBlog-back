package com.chen.LeoBlog.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.chen.LeoBlog.base.ResultInfo;
import com.chen.LeoBlog.base.SocketPool;
import com.chen.LeoBlog.constant.RedisConstant;
import com.chen.LeoBlog.po.ChatConnection;
import com.chen.LeoBlog.po.ChatRecord;
import com.chen.LeoBlog.service.ChatConnectionService;
import com.chen.LeoBlog.service.ChatRecordService;
import com.chen.LeoBlog.service.SocketService;
import com.chen.LeoBlog.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.*;

import static com.chen.LeoBlog.base.SocketPool.getSessionMap;


// 注入容器
@RestController
@Slf4j
// 表明这是一个websocket服务的端点
@ServerEndpoint("/net/{userId}")
public class SocketEndPointController {
    public static SocketEndPointController socketEndpoint; //public极为重要
    private final String key = RedisConstant.CHAT_ALL_LIST;

    @Autowired
    private ChatConnectionService chatConnectionService;
    @Autowired
    private ChatRecordService chatRecordService;
    @Autowired
    private UserService userService;
    @Autowired
    private SocketService socketService;
    @Autowired
    private StringRedisTemplate redisTemplate;

    @PostConstruct
    public void init() {
        socketEndpoint = this;
        socketEndpoint.chatConnectionService = this.chatConnectionService;
        socketEndpoint.chatRecordService = this.chatRecordService;
        socketEndpoint.userService = this.userService;
    }

    @PostMapping("/net/list/status")
    public ResultInfo getStatus(@RequestBody Map<String, Object> map) {

        String ids = map.get("ids").toString();
        ids = ids.substring(1, ids.length() - 1);
        //将字符串转换为数组,并去除空格
        String[] idArr = ids.split(", ");
        List<Integer> list = new ArrayList<>(idArr.length);
        Map<Long, Session> sessionMap = getSessionMap();
        for (String id : idArr) {
            if (sessionMap.containsKey(Long.parseLong(id))) {
                list.add(1);
            } else {
                list.add(0);
            }
        }
        return ResultInfo.success(list);

    }


    @OnOpen
    public void onOpen(@PathParam("userId") Long userId, Session session) {
        log.info("新连接->userId:{},session:{}", userId, session);
        SocketPool.add(userId, session);
        SocketPool.getSessionMap().forEach((k, v) -> {
            if (k != userId) {
                Map<String, Object> map = new HashMap<>();
                map.put("userId", 1);
                map.put("receiveId", k);
                map.put("message", socketEndpoint.userService.query().eq("user_id", userId).one().getUserName() + "已连接");
                map.put("update_time", DateUtil.now());
                map.put("type", 1); // 1:上线通知 2:下线通知 3:聊天消息
                socketEndpoint.socketService.sendMessage(v, JSONUtil.toJsonStr(map));
            }
        });
        if (socketEndpoint.redisTemplate.opsForZSet().rank(key, userId.toString()) == null) {
            socketEndpoint.redisTemplate.opsForZSet().add(key, userId.toString(), new Date().getTime());
        }
    }

    @OnMessage
    public void onMessage(@RequestBody String jsonStr) {
        log.info("收到客户端消息:{}", jsonStr);
        Map<String, Object> map = JSONUtil.toBean(jsonStr, Map.class);
        Long userId = Long.parseLong(map.get("userId").toString());
        Long receiverId = Long.parseLong(map.get("receiverId").toString());
        SocketPool.getSessionMap().forEach((k, v) -> {
            if (StrUtil.equals(k.toString(), receiverId.toString())) {
                socketEndpoint.socketService.sendMessage(v, JSONUtil.toJsonStr(map));
            }
        });

        try {
            // 保存聊天记录
            ChatRecord chatRecord = new ChatRecord();
            chatRecord.setUserId(userId);
            chatRecord.setReceiverId(receiverId);
            chatRecord.setRecordContent((String) map.get("message"));
            chatRecord.setRecordUpdateTime(new Date());
            socketEndpoint.redisTemplate.opsForZSet().add(RedisConstant.CHAT_USER_LIST + userId, receiverId.toString(), new Date().getTime());
            socketEndpoint.chatRecordService.save(chatRecord);
            // 保存聊天连接
            ChatConnection chatConnection = new ChatConnection();
            chatConnection.setUserId(userId);
            chatConnection.setChatLastTime(new Date());
            chatConnection.setChatUserId(receiverId);
            socketEndpoint.chatConnectionService.query()
                    .eq("user_id", userId).eq("chat_user_id", receiverId)
                    .or()
                    .eq("user_id", receiverId).eq("chat_user_id", userId)
                    .orderByDesc("chat_last_time").last("limit 1")
                    .oneOpt()
                    .ifPresentOrElse(
                            chatConnection1 -> {
                                chatConnection.setChatLastTime(new Date());
                                socketEndpoint.chatConnectionService.updateById(chatConnection);
                            },
                            () -> socketEndpoint.chatConnectionService.save(chatConnection)
                    );

        } catch (Exception e) {
            log.error("保存聊天记录失败", e);
        }


    }

    @OnClose
    public void onClose(@PathParam("userId") Long userId, Session session) {
        log.info("连接关闭->userId: {}", userId);
        SocketPool.remove(userId);
        socketEndpoint.redisTemplate.opsForZSet().remove(key, userId.toString());
        SocketPool.getSessionMap().forEach((k, v) -> {
            if (!StrUtil.equals(k.toString(), userId.toString())) {
                Map<String, Object> map = new HashMap<>();
                map.put("userId", 1);
                map.put("receiveId", k);
                map.put("message", socketEndpoint.userService.query().eq("user_id", userId).one().getUserName() + "已断开连接");
                map.put("update_time", DateUtil.now());
                map.put("type", 2); // 1:上线通知 2:下线通知 3:聊天消息
                socketEndpoint.socketService.sendMessage(v, JSONUtil.toJsonStr(map));
            }
        });

    }

    @OnError
    public void onError(@PathParam("userId") Long userId, Throwable error) {
        onClose(userId, SocketPool.getSessionMap().get(userId));
        log.error("发生错误->userId:{}", userId, error);
    }
}