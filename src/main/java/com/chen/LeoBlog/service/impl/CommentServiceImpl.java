package com.chen.LeoBlog.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chen.LeoBlog.activityEvent.Activity;
import com.chen.LeoBlog.activityEvent.ActivityData;
import com.chen.LeoBlog.activityEvent.ActivityEnum;
import com.chen.LeoBlog.base.ResultInfo;
import com.chen.LeoBlog.dto.UserDTO;
import com.chen.LeoBlog.mapper.CommentMapper;
import com.chen.LeoBlog.po.Article;
import com.chen.LeoBlog.po.Comment;
import com.chen.LeoBlog.po.User;
import com.chen.LeoBlog.publisher.ActivityEventPublisher;
import com.chen.LeoBlog.service.ArticleService;
import com.chen.LeoBlog.service.CommentService;
import com.chen.LeoBlog.service.MessageService;
import com.chen.LeoBlog.service.UserService;
import com.chen.LeoBlog.utils.IdUtil;
import com.chen.LeoBlog.utils.MessageUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author 1
 * @description 针对表【lb_comment】的数据库操作Service实现
 * @createDate 2022-10-14 17:36:10
 */
@Service
@Slf4j
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment>
        implements CommentService {
    @Autowired
    private UserService userService;
    @Autowired
    private ArticleService articleService;
    @Resource
    private MessageUtil messageUtil;
    @Resource
    private MessageService messageService;
    @Resource
    private IdUtil idUtil;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Resource
    private ActivityEventPublisher activityEventPublisher;

    /**
     * 获取某一用户的评论总数
     *
     * @param userId
     * @return
     */
    @Override
    public ResultInfo getCommentSumByUserId(Long userId) {
        log.debug("userId: [{}]", userId);
        Integer sum = query().eq("user_id", userId).eq("comment_parent_id", -1).count();
        return ResultInfo.success(sum);
    }

    @Override
    public ResultInfo getComment(Long commentId) {
        log.debug("commentId: [{}]", commentId);
        Comment comment = query().eq("comment_id", commentId).one();
        if (comment == null) {
            return ResultInfo.fail("该评论不存在");
        }
        return ResultInfo.success(comment);
    }

    @Override
    public ResultInfo getCommentList(Long articleId) {
        log.debug("articleId: [{}]", articleId);
        //先取一级评论
        List<Comment> list1 = query().eq("article_id", articleId).eq("comment_parent_id", -1).list();
        List<User> users1 = list1.stream().map(comment -> userService.query().eq("user_id", comment.getUserId()).one()).toList();
        //再取二级评论
        List<List<Comment>> list2 = new ArrayList<>();
        List<List<User>> users2 = new ArrayList<>();
        list1.forEach(comment -> {
            List<Comment> list = query().eq("comment_parent_id", comment.getCommentId())
                    .eq("article_id", comment.getArticleId()).orderByAsc("comment_update_time").list();
            List<User> users = list.stream().map(comment1 -> userService.query().eq("user_id", comment1.getUserId()).one()).toList();
            if (list.size() != 0) {
                list2.add(list);
                users2.add(users);
            } else {
                list2.add(new ArrayList<>());
                users2.add(new ArrayList<>());
            }
        });
        List<Object> result = new ArrayList<>();
        for (int i = 0; i < list1.size(); i++) {
            Map<String, Object> map = new HashMap<>();
            BeanUtil.beanToMap(list1.get(i), map, false, true);
            map.put("user", BeanUtil.copyProperties(users1.get(i), UserDTO.class));
            ArrayList<Object> objects = new ArrayList<>();
            List<User> users = users2.get(i);
            List<Comment> comments = list2.get(i);
            comments.forEach(comment -> {
                Map<String, Object> map1 = new HashMap<>();
                BeanUtil.beanToMap(comment, map1, false, true);
                map1.put("user", BeanUtil.copyProperties(users.get(comments.indexOf(comment)), UserDTO.class));
                objects.add(map1);
            });
            map.put("value", objects);
            result.add(map);
        }
        return ResultInfo.success(result);
    }

    @Override
    public ResultInfo getCommentListByUserId(Long userId) {
        log.debug("userId: [{}]", userId);
        try {
            //先取一级评论
            List<Comment> list1 = query().eq("user_id", userId).eq("comment_parent_id", -1).list();
            List<Article> articles = list1.stream().map(comment -> articleService.query().eq("article_id", comment.getArticleId()).one()).toList();
            if (list1.size() == 0) {
                return ResultInfo.success(new ArrayList<>());
            }
            List<User> users1 = list1.stream().map(comment -> userService.query().eq("user_id", comment.getUserId()).one()).toList();
            //再取二级评论
            List<List<Comment>> list2 = new ArrayList<>();
            List<List<User>> users2 = new ArrayList<>();
            list1.forEach(comment -> {
                List<Comment> list = query().eq("comment_parent_id", comment.getCommentId())
                        .eq("article_id", comment.getArticleId()).orderByAsc("comment_update_time").list();
                List<User> users = list.stream().map(comment1 -> userService.query().eq("user_id", comment1.getUserId()).one()).toList();
                if (list.size() != 0) {
                    list2.add(list);
                    users2.add(users);
                } else {
                    list2.add(new ArrayList<>());
                    users2.add(new ArrayList<>());
                }
            });
            List<Object> result = new ArrayList<>();
            for (int i = 0; i < list1.size(); i++) {
                Map<String, Object> map = new HashMap<>();
                BeanUtil.beanToMap(list1.get(i), map, false, true);
                map.put("user", BeanUtil.copyProperties(users1.get(i), UserDTO.class));
                ArrayList<Object> objects = new ArrayList<>();
                List<User> users = users2.get(i);
                List<Comment> comments = list2.get(i);
                comments.forEach(comment -> {
                    Map<String, Object> map1 = new HashMap<>();
                    BeanUtil.beanToMap(comment, map1, false, true);
                    map1.put("user", BeanUtil.copyProperties(users.get(comments.indexOf(comment)), UserDTO.class));
                    objects.add(map1);
                });
                map.put("value", objects);
                map.put("articleId", articles.get(i).getArticleId());
                map.put("articleTitle", articles.get(i).getArticleTitle());
                result.add(map);
            }
            return ResultInfo.success(result);
        } catch (Exception e) {
            log.error("getCommentListByUserId", e);
            return ResultInfo.fail("获取评论列表失败");
        }

    }

    @Override
    public ResultInfo addComment(Map<String, Object> map) {
        Comment comment = BeanUtil.toBean(map, Comment.class);
        comment.setCommentUpdateTime(new Date());
        boolean isSuccess = save(comment);
        articleService.update().setSql("article_comments = article_comments + 1").eq("article_id", comment.getArticleId()).update();
        Article article = articleService.query().eq("article_id", comment.getArticleId()).one();

//        String commentMessage = messageUtil.getCommentMessage("", article.getArticleTitle());

        Long receiverId = comment.getReceiverId();
        // 确认接收者不是自己
        Long userId = comment.getUserId();
//        if (!userId.equals(receiverId)) {
//            Long msgId = idUtil.nextId(Message.class);
//            messageService.saveActivityMessage(new Message(msgId, userId, receiverId, commentMessage, MsgTypeEnum.COMMENT_ARTICLE, comment.getArticleId() + ""));
//            redisTemplate.opsForZSet().add(RedisConstant.MESSAGE_BOX_PREFIX + receiverId, msgId + "", System.currentTimeMillis());
//        }
        ActivityData activityData = ActivityData.builder().userId(receiverId)
                .commentId(comment.getCommentParentId())
                .commentContent(comment.getCommentContent())
                .articleId(article.getArticleId())
                .articleTitle(article.getArticleTitle())
                .build();
        Integer type;
        if (isSuccess) {
            // 判断是回复还是评论
            if (comment.getCommentParentId() == -1) {
                // 一级，评论
                // 封装活动事件()
                type = ActivityEnum.ARTICLE_COMMENT.getActivityEventId();
            } else {
                type = ActivityEnum.ARTICLE_COMMENT_REPLY.getActivityEventId();
            }

            Activity activity = Activity.builder()
                    .type(type)
                    .targetId(receiverId).userId(userId)
                    .createTime(new Date()).activityData(activityData).build();
            activityEventPublisher.publish(activity);
            return ResultInfo.success("评论成功");
        }
        return ResultInfo.fail("评论失败");
    }

    @Override
    public ResultInfo deleteCommentByArticleId(Long articleId) {
        log.debug("articleId: [{}]", articleId);
        boolean isSuccess = update().eq("article_id", articleId).remove();
        if (!isSuccess) {
            return ResultInfo.fail("删除失败");
        }
        return ResultInfo.success("删除成功");
    }

    @Override
    public ResultInfo deleteCommentByUserId(Long userId) {
        log.debug("userId: [{}]", userId);
        boolean isSuccess = update().eq("user_id", userId).remove();
        if (!isSuccess) {
            return ResultInfo.fail("删除失败");
        }
        return ResultInfo.success("删除成功");
    }

    @Override
    public ResultInfo deleteComment(Long commentId) {
        log.debug("commentId: [{}]", commentId);
        boolean isSuccess = removeById(commentId);
        if (!isSuccess) {
            return ResultInfo.fail("删除失败");
        }
        return ResultInfo.success("删除成功");
    }
}




