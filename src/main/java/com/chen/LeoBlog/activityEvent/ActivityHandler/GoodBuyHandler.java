package com.chen.LeoBlog.activityEvent.ActivityHandler;

import com.chen.LeoBlog.activityEvent.Activity;
import com.chen.LeoBlog.activityEvent.ActivityEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


@Component
@Slf4j
public class GoodBuyHandler extends AbstractActivityHandler {


    @Override
    public String generateContent(Activity activity) {
        return null;
    }


    @Override
    public String generateRouter(Activity activity) {
        Long badgeId = activity.getActivityData().getBadgeId();
        if (badgeId != null) {
            return "/badgeShow/" + badgeId;
        }
        return "/error";
    }

    @Override
    public ActivityEnum getActivityEventType() {
        return ActivityEnum.GOODS_BUY;
    }

    @Override
    public String generateTitle(Activity activity) {
        return "购买了徽章：" + activity.getActivityData().getBadgeName();
    }

}
