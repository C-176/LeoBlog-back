package com.chen.LeoBlog.activityEvent.ActivityEventHandler;

import com.chen.LeoBlog.activityEvent.ActivityEvent;
import com.chen.LeoBlog.activityEvent.ActivityEventEnum;
import com.chen.LeoBlog.activityEvent.EventData;
import com.chen.LeoBlog.po.Comment;
import com.chen.LeoBlog.service.CommentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;


@Component
@Slf4j
public class ArticleCommentReplyHandler extends AbstractActivityEventHandler {

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
        return ActivityEventEnum.ARTICLE_COMMENT_REPLY;
    }

    @Override
    public String generateTitle(ActivityEvent activityEvent) {
        Long commentId = activityEvent.getEventData().getCommentId();
        Comment comment = commentService.lambdaQuery().eq(Comment::getCommentId, commentId).one();
        String commentContent = comment.getCommentContent();
        return "回复了评论：" + commentContent;
    }
}
