package com.chen.LeoBlog.activityEvent.ActivityHandler;

import com.chen.LeoBlog.activityEvent.Activity;
import com.chen.LeoBlog.activityEvent.ActivityData;
import com.chen.LeoBlog.activityEvent.ActivityEnum;
import com.chen.LeoBlog.po.Comment;
import com.chen.LeoBlog.service.CommentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;


@Component
@Slf4j
public class ArticleCommentReplyHandler extends AbstractActivityHandler {

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
        return ActivityEnum.ARTICLE_COMMENT_REPLY;
    }

    @Override
    public String generateTitle(Activity activity) {
        Long commentId = activity.getActivityData().getCommentId();
        Comment comment = commentService.lambdaQuery().eq(Comment::getCommentId, commentId).one();
        String commentContent = comment.getCommentContent();
        return "回复了评论：" + commentContent;
    }
}
