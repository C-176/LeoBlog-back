package com.chen.LeoBlog.websocket;


import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.chen.LeoBlog.annotation.FrequencyControl;
import com.chen.LeoBlog.base.SocketPool;
import com.chen.LeoBlog.config.ThreadPoolConfig;
import com.chen.LeoBlog.constant.RedisConstant;
import com.chen.LeoBlog.dto.TokenBucketFrequencyControlDTO;
import com.chen.LeoBlog.exception.CommonErrorEnum;
import com.chen.LeoBlog.exception.FrequencyControlException;
import com.chen.LeoBlog.frequencycontrol.FrequencyControlStrategyFactory;
import com.chen.LeoBlog.frequencycontrol.FrequencyControlUtil;
import com.chen.LeoBlog.po.ChatConnection;
import com.chen.LeoBlog.po.ChatRecord;
import com.chen.LeoBlog.service.ChatConnectionService;
import com.chen.LeoBlog.service.ChatRecordService;
import com.chen.LeoBlog.service.UserService;
import com.chen.LeoBlog.utils.AssertUtil;
import com.chen.LeoBlog.utils.JWTUtil;
import com.chen.LeoBlog.websocket.enums.WebScoketDataEnum;
import com.chen.LeoBlog.websocket.vo.WebSocketData;
import com.chen.LeoBlog.websocket.vo.WebSocketDataWithUserId;
import com.yupi.yucongming.dev.client.YuCongMingClient;
import com.yupi.yucongming.dev.common.BaseResponse;
import com.yupi.yucongming.dev.model.DevChatRequest;
import com.yupi.yucongming.dev.model.DevChatResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.websocket.RemoteEndpoint;
import javax.websocket.Session;
import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.Set;


@Service
@Slf4j

@EnableAspectJAutoProxy(exposeProxy = true)
public class SocketService {

    private final String key = RedisConstant.ONLINE_ALL;

    @Resource
    private ChatConnectionService chatConnectionService;
    @Resource
    private ChatRecordService chatRecordService;
    @Resource
    private UserService userService;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Resource
    private YuCongMingClient yuCongMingClient;
    @Resource(name = ThreadPoolConfig.WS_EXECUTOR)
    @Lazy
    private ThreadPoolTaskExecutor asyncExecutor;

    @Resource
    private WebSocketTimer webSocketTimer;

    public Map<Long, Session> getSessionMap() {
        return SocketPool.getSessionMap();
    }


    public boolean sendToSession(Session session, WebSocketData webSocketData) {
        if (session == null) return false;
        synchronized (session) {
            try {
                RemoteEndpoint.Basic basic = session.getBasicRemote();
                assert basic != null;
                basic.sendText(JSONUtil.toJsonStr(webSocketData));
            } catch (Exception e) {
                log.error("消息发送异常，异常情况: {}", e.getMessage());
                AssertUtil.isFalse(false, CommonErrorEnum.SYSTEM_ERROR);
                return false;
            }
            return true;
        }
    }

    public void sendGroupMessage(WebSocketData webSocketData, Map<Long, Session> sessionMap) {
        // 群组ID
        Long groupId = webSocketData.getReceiveId();
        // 群组成员
        Set<String> members = redisTemplate.opsForZSet().range(RedisConstant.CHAT_GROUP_LIST + groupId, 0, -1L);
        if (members == null) return;
        members.forEach(member -> {
            Session session = sessionMap.get(Long.valueOf(member));
            if (session != null) {
                sendToSession(session, webSocketData);
            }
        });
    }

    public void systemNotice(String message) {
        log.info("广播：系统通知");
        SocketPool.getSessionMap().forEach((keyId, session) -> WebSocketData.systemNotice(message));
    }

    public void onOpen(Long userId, String token, Session session) {
        if (StrUtil.isBlank(token)) {
            sendToSession(session, WebSocketData.authFailResponse());
            try {
                session.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }
        try {
            String jwtUserId = String.valueOf(JWTUtil.parseJwtUserId(token));
            if (!jwtUserId.equals(userId.toString())) {
                sendToSession(session, WebSocketData.authFailResponse());
                try {
                    session.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return;
            }
        } catch (Exception e) {
            sendToSession(session, WebSocketData.authFailResponse());
            try {
                session.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        //根据token去redis中查询对应的用户信息
        String s = redisTemplate.opsForValue().get(RedisConstant.USER_LOGIN + userId);
        //如果查不到信息，或者为空，说明用户登陆信息，已经过期，需要重新登陆。
        if (StrUtil.isBlank(s)) {
            sendToSession(session, WebSocketData.authFailResponse());
            return;
        }
        webSocketTimer.updateHeartbeatRecord(userId);
        SocketPool.add(userId, session); // 添加到在线用户列表
        // 添加到redis在线用户中
        redisTemplate.opsForZSet().add(key, userId.toString(), new Date().getTime());

        Map<Long, Session> sessionMap = getSessionMap();
        // 向好友发送上线通知
        Set<String> friends = redisTemplate.opsForZSet().range(RedisConstant.CHAT_FRIEND_LIST + userId, 0, -1L);
        if (friends == null) return;
        friends.forEach(f -> {
            if (sessionMap.containsKey(Long.parseLong(f))) {
                sendToSession(sessionMap.get(Long.parseLong(f)), WebSocketData.onlineNotice(userId));
            }
        });


    }

    //    @FrequencyControl(target = FrequencyControl.Target.EL, spEl = "#session", time = 5, count = 2)
    public void onMessage(String jsonStr, Session session) {
        WebSocketDataWithUserId webSocketData = JSONUtil.toBean(jsonStr, WebSocketDataWithUserId.class);
        Map<Long, Session> onlineUsers = getSessionMap();
//        FrequencyControlDTO build = FrequencyControlDTO.builder().count(2).time(5).unit(TimeUnit.SECONDS).key("single_chat" + webSocketData.getUserId() + "").build();
        TokenBucketFrequencyControlDTO build = TokenBucketFrequencyControlDTO.builder()

                .tokens(1).capacity(10).refillPeriod(60 * 1000).refillTokens(5).capacity(10).build();
        build.setKey("single_chat:" + webSocketData.getUserId() + "");
        switch (WebScoketDataEnum.of(webSocketData.getType())) {
            case HEART_BEAT -> heartBeatHandler(session, webSocketData);
            case SINGLE_CHAT -> {
                try {
                    FrequencyControlUtil.executeWithFrequencyControl(FrequencyControlStrategyFactory.TOKEN_BUCKET_FREQUENCY_CONTROLLER,
                            build, () -> singleChatHandler(webSocketData, onlineUsers));
                } catch (FrequencyControlException ex) {
                    if (session != null) {
                        sendToSession(session, WebSocketData.frequencyControlNotice());
                    }
                } catch (Throwable e) {
                    throw new RuntimeException(e);
                }
            }

            case AI_CHAT -> aiChatHandler(webSocketData, onlineUsers);
            case GROUP_CHAT -> groupChatHandler(webSocketData, onlineUsers);
        }


    }

    public void heartBeatHandler(Session session, WebSocketDataWithUserId webSocketData) {
        sendToSession(session, WebSocketData.heartBeatResponse());
        webSocketTimer.updateHeartbeatRecord(webSocketData.getUserId());
    }

    public void groupChatHandler(WebSocketDataWithUserId webSocketData, Map<Long, Session> onlineUsers) {
        sendGroupMessage(webSocketData, onlineUsers);
    }

    public void aiChatHandler(WebSocketDataWithUserId webSocketData, Map<Long, Session> onlineUsers) {
        String jsonStr = webSocketData.getContent();
        ChatRecord record = JSONUtil.toBean(jsonStr, ChatRecord.class);
        Long userId = record.getUserId();
        Long receiverId = record.getReceiverId();
        // 保存聊天记录
        sendToSession(onlineUsers.get(userId), WebSocketData.singleChatData(receiverId, JSONUtil.toJsonStr(record)));
        chatRecordService.save(record);
        // AI对话
        DevChatRequest devChatRequest = new DevChatRequest();
        // 修改模型Id
        devChatRequest.setModelId(1654785040361893889L);
        devChatRequest.setMessage(record.getRecordContent());
        BaseResponse<DevChatResponse> response = yuCongMingClient.doChat(devChatRequest);

        ChatRecord build = ChatRecord.builder().recordUpdateTime(new Date()).userId(receiverId).receiverId(userId).build();
        if (response.getCode() == 0) {
            String content = response.getData().getContent();
            build.setRecordContent(content);
        } else {
            log.error("AI回复失败:{}", response);
            build.setRecordContent("我还小，不知道你在说什么");
        }
        sendToSession(onlineUsers.get(userId), WebSocketData.singleChatData(receiverId, JSONUtil.toJsonStr(build)));
        asyncSaveRecord(build);
    }

    @FrequencyControl(target = FrequencyControl.Target.EL, spEl = "#webSocketData", time = 5, count = 2)
    public void singleChatHandler(WebSocketDataWithUserId webSocketData, Map<Long, Session> onlineUsers) {
        String jsonStr = webSocketData.getContent();
        ChatRecord record = JSONUtil.toBean(jsonStr, ChatRecord.class);
        Long receiverId = record.getReceiverId();

        if (receiverId == -1) { // 聊天室消息
            asyncExecutor.execute(() -> {
                onlineUsers.forEach((k, v) -> sendToSession(onlineUsers.get(k), WebSocketData.singleChatData(-1L, JSONUtil.toJsonStr(record))));
                asyncSaveRecord(record);
            });
        } else if (receiverId == 1) {
            aiChatHandler(webSocketData, onlineUsers);
        } else {
            Session session = onlineUsers.get(receiverId);
            if (session != null)
                sendToSession(session, WebSocketData.singleChatData(receiverId, JSONUtil.toJsonStr(record)));
            session = onlineUsers.get(record.getUserId());
            if (session != null)
                sendToSession(session, WebSocketData.singleChatData(receiverId, JSONUtil.toJsonStr(record)));
            asyncSaveRecord(record);
        }

    }

    public void asyncSaveRecord(ChatRecord record) {
        try {
            asyncExecutor.execute(() -> {
                Long userId = record.getUserId(), receiverId = record.getReceiverId();
                chatRecordService.save(record);
                if (receiverId != 1)
                    redisTemplate.opsForZSet().add(RedisConstant.CHAT_FRIEND_LIST + userId, receiverId.toString(), new Date().getTime());
                // 保存聊天连接
                if (receiverId == -1 || receiverId == 1) return;
                ChatConnection chatConnection = new ChatConnection();
                chatConnection.setUserId(userId);
                chatConnection.setChatLastTime(new Date());
                chatConnection.setChatUserId(receiverId);
                chatConnectionService.query().eq("user_id", userId).eq("chat_user_id", receiverId).or().eq("user_id", receiverId).eq("chat_user_id", userId).orderByDesc("chat_last_time").last("limit 1").oneOpt().ifPresentOrElse(chatConnection1 -> {
                    chatConnection.setChatLastTime(new Date());
                    chatConnectionService.updateById(chatConnection);
                }, () -> chatConnectionService.save(chatConnection));
            });
        } catch (Exception e) {
            log.error("保存聊天记录失败", e);
        }
    }

    public void onClose(Long userId) {
        SocketPool.remove(userId);
        redisTemplate.opsForZSet().remove(key, userId.toString());
        Map<Long, Session> sessionMap = getSessionMap();
        // 向好友发送下线通知
        Set<String> friends = redisTemplate.opsForZSet().range(RedisConstant.CHAT_FRIEND_LIST + userId, 0, -1L);
        if (friends == null) return;
        friends.forEach(f -> {
            if (sessionMap.containsKey(Long.parseLong(f))) {
                // 使用线程池异步发送消息
                //TODO:为什么使用线程池发送消息，而不是直接发送消息
                asyncExecutor.execute(() -> sendToSession(sessionMap.get(Long.parseLong(f)), WebSocketData.offlineNotice(userId)));
            }
        });
    }

    public void onError(Long userId, Throwable error) {
        onClose(userId);
//        log.error("webSocket发生错误->关闭userId:{}", userId, error);
    }
}