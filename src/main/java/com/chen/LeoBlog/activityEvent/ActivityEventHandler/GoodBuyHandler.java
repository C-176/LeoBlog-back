package com.chen.LeoBlog.activityEvent.ActivityEventHandler;

import com.chen.LeoBlog.activityEvent.ActivityEvent;
import com.chen.LeoBlog.activityEvent.ActivityEventEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


@Component
@Slf4j
public class GoodBuyHandler extends AbstractActivityEventHandler {


    @Override
    public String generateContent(ActivityEvent activityEvent) {
        return null;
    }


    @Override
    public String generateRouter(ActivityEvent activityEvent) {
        Long badgeId = activityEvent.getEventData().getBadgeId();
        if (badgeId != null) {
            return "/badgeShow/" + badgeId;
        }
        return "/error";
    }

    @Override
    public ActivityEventEnum getActivityEventType() {
        return ActivityEventEnum.GOODS_BUY;
    }

    @Override
    public String generateTitle(ActivityEvent activityEvent) {
        return "购买了徽章：" + activityEvent.getEventData().getBadgeName();
    }

}
