package com.chen.LeoBlog.activityEvent.ActivityHandler;

import com.chen.LeoBlog.activityEvent.Activity;
import com.chen.LeoBlog.activityEvent.ActivityData;
import com.chen.LeoBlog.activityEvent.ActivityEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


@Component
@Slf4j
public class ArticleCollectHandler extends AbstractActivityHandler {


    @Override
    public String generateContent(Activity activity) {
        return null;
    }


    @Override
    public String generateRouter(Activity activity) {
        ActivityData activityData = activity.getActivityData();
        Long articleId = activityData.getArticleId();
        if (articleId != null) {
            return "/article/" + articleId;
        }
        return "/error";
    }

    @Override
    public ActivityEnum getActivityEventType() {
        return ActivityEnum.ARTICLE_COLLECT;
    }

    @Override
    public String generateTitle(Activity activity) {
        return "收藏了文章：" + activity.getActivityData().getArticleTitle();
    }
}
