package com.chen.LeoBlog.listener;

import cn.hutool.json.JSONUtil;
import com.chen.LeoBlog.activityEvent.Activity;
import com.chen.LeoBlog.activityEvent.ActivityEvent;
import com.chen.LeoBlog.service.KafkaService;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class ActivityEventListener {
    public static final String ACTIVITY_TOPIC = "activity";
    @Resource
    private KafkaService kafkaService;

    @EventListener
    public void onApplicationEvent(ActivityEvent<Activity> event) {
        Activity activity = (Activity) event.getSource();
        // 发布到对应消息队列
        kafkaService.sendMessage(ACTIVITY_TOPIC, JSONUtil.toJsonStr(activity));
    }
}
