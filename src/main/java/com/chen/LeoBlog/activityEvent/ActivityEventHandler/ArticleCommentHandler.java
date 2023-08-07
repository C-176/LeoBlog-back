package com.chen.LeoBlog.activityEvent.ActivityEventHandler;

import com.chen.LeoBlog.activityEvent.ActivityEvent;
import com.chen.LeoBlog.activityEvent.ActivityEventEnum;
import com.chen.LeoBlog.activityEvent.EventData;
import com.chen.LeoBlog.service.CommentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;


@Component
@Slf4j
public class ArticleCommentHandler extends AbstractActivityEventHandler {

    @Resource
    private CommentService commentService;

    @Override
    public String generateContent(ActivityEvent activityEvent) {
        return activityEvent.getEventData().getCommentContent();
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
        return ActivityEventEnum.ARTICLE_COMMENT;
    }

    @Override
    public String generateTitle(ActivityEvent activityEvent) {
        return "评论了文章：" + activityEvent.getEventData().getArticleTitle();
    }
}
