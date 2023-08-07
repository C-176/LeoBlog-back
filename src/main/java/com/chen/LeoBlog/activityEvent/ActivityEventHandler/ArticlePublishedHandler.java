package com.chen.LeoBlog.activityEvent.ActivityEventHandler;

import com.chen.LeoBlog.activityEvent.ActivityEvent;
import com.chen.LeoBlog.activityEvent.ActivityEventEnum;
import com.chen.LeoBlog.activityEvent.EventData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


@Component
@Slf4j
public class ArticlePublishedHandler extends AbstractActivityEventHandler {

    @Override
    public String generateContent(ActivityEvent activityEvent) {
        return null;
    }

    @Override
    public String generateRouter(ActivityEvent activityEvent) {
        EventData eventData = activityEvent.getEventData();
        Long articleId = eventData.getArticleId();
        if (articleId != null) {
            return "/article/" + articleId;
        }
        return "/error";
    }

    @Override
    public ActivityEventEnum getActivityEventType() {
        return ActivityEventEnum.ARTICLE_PUBLISH;
    }

    @Override
    public String generateTitle(ActivityEvent activityEvent) {
        return "发布新文章：" + activityEvent.getEventData().getArticleTitle();
    }
}
