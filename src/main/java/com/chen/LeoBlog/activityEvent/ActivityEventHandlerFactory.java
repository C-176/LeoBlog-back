package com.chen.LeoBlog.activityEvent;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.chen.LeoBlog.activityEvent.ActivityEventHandler.AbstractActivityEventHandler;
import com.chen.LeoBlog.utils.AssertUtil;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ActivityEventHandlerFactory {
    private static final Map<ActivityEventEnum, AbstractActivityEventHandler> ActivityEventHandler_MAP = new ConcurrentHashMap<>();

    public static void register(ActivityEventEnum activityEventEnum, AbstractActivityEventHandler chatAIHandler) {
        ActivityEventHandler_MAP.put(activityEventEnum, chatAIHandler);
    }

    public static AbstractActivityEventHandler getActivityEventHandler(List<ActivityEventEnum> activityEventIds) {
        if (CollectionUtils.isEmpty(activityEventIds)) {
            return null;
        }
        for (ActivityEventEnum activityEventId : activityEventIds) {
            AbstractActivityEventHandler activityEventHandler = ActivityEventHandler_MAP.get(activityEventId);
            if (activityEventHandler != null) {
                return activityEventHandler;
            }
        }
        return null;
    }

    public static void execute(ActivityEvent activityEvent) {
        ActivityEventEnum of = ActivityEventEnum.of(activityEvent.getType());
        AbstractActivityEventHandler activityEventHandler = getActivityEventHandler(Collections.singletonList(of));
        AssertUtil.isNotEmpty(activityEventHandler, "未找到对应的活动事件处理器");
        activityEventHandler.execute(activityEvent);
    }
}
