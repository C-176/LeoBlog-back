package com.chen.LeoBlog.service;

import com.chen.LeoBlog.base.ResultInfo;
import com.chen.LeoBlog.po.Comment;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
* @author 1
* @description 针对表【lb_comment】的数据库操作Service
* @createDate 2022-10-14 17:36:10
*/
public interface CommentService extends IService<Comment> {

    ResultInfo getCommentSumByUserId(Long userId);

    ResultInfo getComment(Long commentId);

    ResultInfo getCommentList(Long articleId);

    ResultInfo getCommentListByUserId(Long userId);

    ResultInfo addComment(Map<String, Object> map);

    ResultInfo deleteCommentByArticleId(Long articleId);

    ResultInfo deleteCommentByUserId(Long userId);

    ResultInfo deleteComment(Long commentId);

}
