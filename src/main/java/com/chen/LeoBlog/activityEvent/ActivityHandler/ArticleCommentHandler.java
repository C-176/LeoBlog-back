package com.chen.LeoBlog.activityEvent.ActivityHandler;

import com.chen.LeoBlog.activityEvent.Activity;
import com.chen.LeoBlog.activityEvent.ActivityData;
import com.chen.LeoBlog.activityEvent.ActivityEnum;
import com.chen.LeoBlog.service.CommentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;


@Component
@Slf4j
public class ArticleCommentHandler extends AbstractActivityHandler {

    @Resource
    private CommentService commentService;

    @Override
    public String generateContent(Activity activity) {
        return activity.getActivityData().getCommentContent();
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
        return ActivityEnum.ARTICLE_COMMENT;
    }

    @Override
    public String generateTitle(Activity activity) {
        return "评论了文章：" + activity.getActivityData().getArticleTitle();
    }
}
