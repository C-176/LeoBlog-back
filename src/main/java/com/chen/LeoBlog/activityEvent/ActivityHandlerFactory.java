package com.chen.LeoBlog.activityEvent;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.chen.LeoBlog.activityEvent.ActivityHandler.AbstractActivityHandler;
import com.chen.LeoBlog.po.Message;
import com.chen.LeoBlog.utils.AssertUtil;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ActivityHandlerFactory {
    private static final Map<ActivityEnum, AbstractActivityHandler> ActivityEventHandler_MAP = new ConcurrentHashMap<>();

    public static void register(ActivityEnum activityEnum, AbstractActivityHandler chatAIHandler) {
        ActivityEventHandler_MAP.put(activityEnum, chatAIHandler);
    }

    public static AbstractActivityHandler getActivityEventHandler(List<ActivityEnum> activityEventIds) {
        if (CollectionUtils.isEmpty(activityEventIds)) {
            return null;
        }
        for (ActivityEnum activityEventId : activityEventIds) {
            AbstractActivityHandler activityEventHandler = ActivityEventHandler_MAP.get(activityEventId);
            if (activityEventHandler != null) {
                return activityEventHandler;
            }
        }
        return null;
    }

    /**
     * 获取对应的活动事件处理器，保存并通知相关用户
     *
     * @param activity
     */
    public static void execute(Activity activity) {
        ActivityEnum of = ActivityEnum.of(activity.getType());
        AbstractActivityHandler activityEventHandler = getActivityEventHandler(Collections.singletonList(of));
        AssertUtil.isNotEmpty(activityEventHandler, "未找到对应的活动事件处理器");
        activityEventHandler.execute(activity);
    }

    /**
     * 随机获取一个事件处理器，保存最终消息
     *
     * @param message
     */
    public static void save(Message message) {
        // 随机获取一个活动事件处理器
        AbstractActivityHandler abstractActivityHandler = ActivityEventHandler_MAP.values().stream().findAny().orElse(null);
        AssertUtil.isFalse(abstractActivityHandler == null, "无活动事件处理器");
        abstractActivityHandler.saveActivityMessage(message);
    }

}
