package com.chen.LeoBlog.activityEvent.ActivityHandler;

import cn.hutool.json.JSONUtil;
import com.chen.LeoBlog.activityEvent.Activity;
import com.chen.LeoBlog.activityEvent.ActivityEnum;
import com.chen.LeoBlog.activityEvent.ActivityHandlerFactory;
import com.chen.LeoBlog.constant.RedisConstant;
import com.chen.LeoBlog.po.Message;
import com.chen.LeoBlog.service.MessageService;
import com.chen.LeoBlog.utils.IdUtil;
import com.chen.LeoBlog.utils.RedisUtils;
import com.chen.LeoBlog.websocket.SocketService;
import com.chen.LeoBlog.websocket.vo.WebSocketData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.websocket.Session;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Slf4j
@Component
public abstract class AbstractActivityHandler {
    @Resource
    private IdUtil idUtil;
    @Resource
    private MessageService messageService;
    @Resource
    private SocketService socketService;

    @PostConstruct
    protected void init() {
        ActivityHandlerFactory.register(getActivityEventType(), this);
    }

    // 记录活动事件
    public Message buildMessage(Activity activity) {
        // 解析出用户id
        Long userId = activity.getUserId();
        Long targetId = activity.getTargetId();
        // 将活动事件转为Message类型
        return Message.builder().userId(userId)
                .messageId(idUtil.nextId(Message.class)).receiverId(targetId)
                .messageUpdateTime(activity.getCreateTime())
                .messageTitle(generateTitle(activity))
                .messageContent(generateContent(activity))
                .messageRedirect(generateRouter(activity))
                .messageType(getActivityEventType().getActivityEventId())
                .build();
    }

    public abstract String generateContent(Activity activity);


    // 保存到redis
    public void saveActivityEventToRedis(Message message) {
        Long userId = message.getUserId();
        Long receiverId = message.getReceiverId();
        Set<Long> idSet = new HashSet<>();
        idSet.add(userId);
        idSet.add(receiverId);
        for (long id : idSet) {
            String key = RedisConstant.ACTIVITY_USER + id;
            try {
                RedisUtils.zAdd(key, JSONUtil.toJsonStr(message), message.getMessageUpdateTime().getTime());
            } catch (Exception e) {
                log.error("保存活动事件出错", e);
            }
        }

    }

    public void sendActivityEventToSession(Message message) {
        Map<Long, Session> sessionMap = socketService.getSessionMap();
        HashSet<Long> ids = new HashSet<>();
        ids.add(message.getUserId());
        ids.add(message.getReceiverId());
        ids.forEach(id -> {
            if (sessionMap.containsKey(id)) {
                socketService.sendToSession(sessionMap.get(id), WebSocketData.newActivityNotice(JSONUtil.toJsonStr(message)));
            }
        });
    }

    // 执行事件

    /**
     * 保存并通过websocket发送通知
     *
     * @param activity
     */
    public void execute(Activity activity) {
        Message entity = buildMessage(activity);
        sendActivityEventToSession(entity);
        saveActivityMessage(entity);
    }

    public void saveActivityMessage(Message entity) {
        saveActivityEventToRedis(entity);
        messageService.save(entity);
    }


    public abstract String generateRouter(Activity activity);

    public abstract ActivityEnum getActivityEventType();

    public abstract String generateTitle(Activity activity);


}
