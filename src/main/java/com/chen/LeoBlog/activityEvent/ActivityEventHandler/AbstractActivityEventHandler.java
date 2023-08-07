package com.chen.LeoBlog.activityEvent.ActivityEventHandler;

import cn.hutool.json.JSONUtil;
import com.chen.LeoBlog.activityEvent.ActivityEvent;
import com.chen.LeoBlog.activityEvent.ActivityEventEnum;
import com.chen.LeoBlog.activityEvent.ActivityEventHandlerFactory;
import com.chen.LeoBlog.constant.RedisConstant;
import com.chen.LeoBlog.po.Message;
import com.chen.LeoBlog.service.MessageService;
import com.chen.LeoBlog.utils.IdUtil;
import com.chen.LeoBlog.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Set;

@Slf4j
@Component
public abstract class AbstractActivityEventHandler {
    @Resource
    private IdUtil idUtil;
    @Resource
    private MessageService messageService;

    @PostConstruct
    protected void init() {
        ActivityEventHandlerFactory.register(getActivityEventType(), this);
    }

    // 记录活动事件
    public Message buildMessage(ActivityEvent activityEvent) {
        // 解析出用户id
        Long userId = activityEvent.getUserId();
        Long targetId = activityEvent.getTargetId();
        // 将活动事件转为Message类型
        return Message.builder().userId(userId)
                .messageId(idUtil.nextId(Message.class)).receiverId(targetId)
                .messageUpdateTime(activityEvent.getCreateTime())
                .messageTitle(generateTitle(activityEvent))
                .messageContent(generateContent(activityEvent))
                .messageRedirect(generateRouter(activityEvent))
                .messageType(getActivityEventType().getActivityEventId())
                .build();
    }

    public abstract String generateContent(ActivityEvent activityEvent);


    // 保存到redis
    public void saveActivityEventToRedis(Message message) {
        Long userId = message.getUserId();
        Long receiverId = message.getReceiverId();
        Set<Long> idSet = Set.of(userId, receiverId);
        for (long id : idSet) {
            String key = RedisConstant.ACTIVITY_USER + id;
            try {
                RedisUtils.zAdd(key, JSONUtil.toJsonStr(message), message.getMessageUpdateTime().getTime());
            } catch (Exception e) {
                log.error("保存活动事件出错", e);
            }
        }
    }

    // 执行事件
    public void execute(ActivityEvent activityEvent) {
        Message entity = buildMessage(activityEvent);
        saveActivityEventToRedis(entity);
        messageService.save(entity);
    }


    public abstract String generateRouter(ActivityEvent activityEvent);

    public abstract ActivityEventEnum getActivityEventType();

    public abstract String generateTitle(ActivityEvent activityEvent);


}
