package com.chen.LeoBlog.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
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
import com.chen.LeoBlog.utils.IdUtil;
import com.yupi.yucongming.dev.client.YuCongMingClient;
import com.yupi.yucongming.dev.common.BaseResponse;
import com.yupi.yucongming.dev.model.DevChatRequest;
import com.yupi.yucongming.dev.model.DevChatResponse;
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
import java.util.concurrent.Executor;

import static com.chen.LeoBlog.base.SocketPool.getSessionMap;


// 注入容器
@RestController
@Slf4j
@CrossOrigin
// 表明这是一个websocket服务的端点
@ServerEndpoint("/net/{userId}")
public class SocketEndPointController {
    public static SocketEndPointController socketEndpoint; //public极为重要
    private final String key = RedisConstant.ONLINE_ALL;

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
    @Resource
    private YuCongMingClient yuCongMingClient;

    @Autowired
    private Executor asyncExecutor;

    @PostConstruct
    public void init() {
        socketEndpoint = this;
        socketEndpoint.chatConnectionService = this.chatConnectionService;
        socketEndpoint.chatRecordService = this.chatRecordService;
        socketEndpoint.userService = this.userService;
        socketEndpoint.asyncExecutor = this.asyncExecutor;
        socketEndpoint.yuCongMingClient = this.yuCongMingClient;
    }

    @PostMapping("/net/list/status")
    public ResultInfo getStatus(@RequestBody Map<String, Object> map) {
        JSONArray ids = JSONUtil.parseArray(map.get("ids"));
        Long[] idArr = ids.toArray(new Long[0]);
        Set<Long> onlineIds = getSessionMap().keySet();
        List<Integer> status = Arrays.stream(idArr)
                .map(id -> onlineIds.contains(id) ? 1 : 0).toList();
        return ResultInfo.success(status);
    }


    @OnOpen
    public void onOpen(@PathParam("userId") Long userId, Session session) {
        log.info("新连接->userId:{},session:{}", userId, session);
        SocketPool.add(userId, session); // 添加到在线用户列表
        // 添加到redis在线用户中
        socketEndpoint.redisTemplate.opsForZSet().add(key, userId.toString(), new Date().getTime());

        Map<Long, Session> sessionMap = getSessionMap();
        // 向好友发送上线通知
        Set<String> friends = socketEndpoint.redisTemplate.opsForZSet().range(RedisConstant.CHAT_FRIEND_LIST + userId, 0, -1L);
        if (friends == null) return;
        friends.forEach(f -> {
            if (sessionMap.containsKey(Long.parseLong(f))) {
                // 使用线程池异步发送消息
                socketEndpoint.asyncExecutor.execute(() -> {
                    //                map.put("type", 1); // 1:上线通知 2:下线通知 3:聊天消息
                    Map<String, Object> messageMap = Map.of("userId", 1,
                            "receiveId", f,
                            "recordContent", socketEndpoint.userService.query().eq("user_id", userId).one().getUserNickname() + " 已上线",
                            "recordUpdateTime", DateUtil.now(), "type", 1);
                    socketEndpoint.socketService.sendMessage(sessionMap.get(Long.parseLong(f)), JSONUtil.toJsonStr(messageMap));
                });
            }
        });


    }

    @OnMessage
    public void onMessage(@RequestBody String jsonStr) {
        log.info("收到客户端消息:{}", jsonStr);
        ChatRecord record = JSONUtil.toBean(jsonStr, ChatRecord.class);
        Long userId = record.getUserId();
        Long receiverId = record.getReceiverId();
        Map<Long, Session> onlineUsers = getSessionMap();
        if (receiverId == -1) { // 聊天室消息
            socketEndpoint.asyncExecutor.execute(
                    () -> onlineUsers
                            .forEach((k, v) -> socketEndpoint.socketService.sendMessage(v, JSONUtil.toJsonStr(record)))
            );
        } else if (receiverId == 1) {
            // AI对话
            DevChatRequest devChatRequest = new DevChatRequest();
            devChatRequest.setModelId(1651468516836098050L);
            devChatRequest.setMessage(record.getRecordContent());
//            BaseResponse<DevChatResponse> response = client.doChat(devChatRequest);
//            System.out.println(response.getData());
            BaseResponse<DevChatResponse> response = socketEndpoint.yuCongMingClient.doChat(devChatRequest);
            System.out.println(response);
            record.setReceiverId(record.getUserId());
            record.setUserId(receiverId);
            record.setRecordUpdateTime(new Date());
            record.setIsSaw(0);
            if (response.getCode() == 0) {
                String content = response.getData().getContent();
                record.setRecordContent(content);
            } else {
                record.setRecordContent("我还小，不知道你在说什么");
            }
            socketEndpoint.socketService.sendMessage(onlineUsers.get(userId), JSONUtil.toJsonStr(record));
            return;


        } else {
            Session session = onlineUsers.get(receiverId);
            if (session != null) socketEndpoint.socketService.sendMessage(session, JSONUtil.toJsonStr(record));
        }
        try {

            socketEndpoint.asyncExecutor.execute(() -> {
                socketEndpoint.redisTemplate.opsForZSet().add(RedisConstant.CHAT_FRIEND_LIST + userId, receiverId.toString(), new Date().getTime());
                socketEndpoint.chatRecordService.save(record);
            });
            socketEndpoint.asyncExecutor.execute(() -> {
                // 保存聊天连接
                if (receiverId == -1) return;
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
            });
        } catch (Exception e) {
            log.error("保存聊天记录失败", e);
        }


    }

    @OnClose
    public void onClose(@PathParam("userId") Long userId) {

        log.info("连接关闭->userId: {}", userId);
        SocketPool.remove(userId);
        socketEndpoint.redisTemplate.opsForZSet().remove(key, userId.toString());
        Map<Long, Session> sessionMap = getSessionMap();
        // 向好友发送上线通知
        Set<String> friends = socketEndpoint.redisTemplate.opsForZSet().range(RedisConstant.CHAT_FRIEND_LIST + userId, 0, -1L);
        if (friends == null) return;
        friends.forEach(f -> {
            if (sessionMap.containsKey(Long.parseLong(f))) {
                // 使用线程池异步发送消息
                socketEndpoint.asyncExecutor.execute(() -> {
                    //                map.put("type", 1); // 1:上线通知 2:下线通知 3:聊天消息
                    Map<String, Object> messageMap = Map.of("userId", 1,
                            "receiveId", f,
                            "recordContent", socketEndpoint.userService.query().eq("user_id", userId).one().getUserNickname() + " 已下线",
                            "recordUpdateTime", DateUtil.now(), "type", 2);
                    socketEndpoint.socketService.sendMessage(sessionMap.get(Long.parseLong(f)), JSONUtil.toJsonStr(messageMap));
                });
            }
        });


    }

    @OnError
    public void onError(@PathParam("userId") Long userId, Throwable error) {
        onClose(userId);
        log.error("webSocket发生错误->关闭userId:{}", userId, error);
    }


}