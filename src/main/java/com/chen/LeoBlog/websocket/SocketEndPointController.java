package com.chen.LeoBlog.websocket;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.chen.LeoBlog.base.ResultInfo;
import com.chen.LeoBlog.constant.RedisConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.*;


// 注入容器
@RestController
@Slf4j
@CrossOrigin
// 表明这是一个websocket服务的端点
@ServerEndpoint("/net/{userId}/{token}")
public class SocketEndPointController {
    public static SocketEndPointController socketEndpoint; //public极为重要

    @Resource
    private SocketService socketService;
    @Autowired
    private StringRedisTemplate redisTemplate;

    @PostConstruct
    public void init() {
        socketEndpoint = this;
    }

    @PostMapping("/net/list/status")
    public ResultInfo<?> getStatus(@RequestBody Map<String, Object> map) {
        JSONArray ids = JSONUtil.parseArray(map.get("ids"));
        String[] idArr = ids.toArray(new String[0]);
        String key = RedisConstant.ONLINE_ALL;
        Set<String> onlineUsers = redisTemplate.opsForZSet().range(key, 0, -1);
        if (onlineUsers == null || onlineUsers.isEmpty()) return ResultInfo.success(Collections.emptyList());
        List<Integer> list = Arrays.stream(idArr)
                .map(id -> onlineUsers.contains(id) || Long.parseLong(id) < 10 ? 1 : 0).toList();
        return ResultInfo.success(list);
    }


    @OnOpen
    public void onOpen(@PathParam("userId") Long userId, @PathParam("token") String token, Session session) {
        socketEndpoint.socketService.onOpen(userId, token, session);
    }

    @OnMessage
    public void onMessage(@RequestBody String webSocketData, Session session) {
        socketEndpoint.socketService.onMessage(webSocketData, session);
    }

    @OnClose
    public void onClose(@PathParam("userId") Long userId) {
        socketEndpoint.socketService.onClose(userId);
    }

    @OnError
    public void onError(@PathParam("userId") Long userId, Throwable error) {
        socketEndpoint.socketService.onError(userId, error);
    }
}