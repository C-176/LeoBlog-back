package com.chen.LeoBlog.controller;

import com.chen.LeoBlog.annotation.Anonymous;
import com.chen.LeoBlog.base.ResultInfo;
import com.chen.LeoBlog.service.CommentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

@Slf4j
@RestController
@CrossOrigin
@RequestMapping("/comment")
public class CommentController {
    @Resource
    private CommentService commentService;

    @Anonymous
    @GetMapping("/list/article/{articleId}")
    public ResultInfo getCommentList(@PathVariable("articleId") Long articleId) {
        return commentService.getCommentList(articleId);
    }

    @Anonymous
    @GetMapping("/{commentId}")
    public ResultInfo getComment(@PathVariable("commentId") Long commentId) {
        return commentService.getComment(commentId);
    }

    @GetMapping("/list/user/{userId}")
    public ResultInfo getCommentListByUserId(@PathVariable("userId") Long userId) {
        return commentService.getCommentListByUserId(userId);
    }

    @GetMapping("/sum/user/{userId}")
    public ResultInfo getCommentSumByUserId(@PathVariable("userId") Long userId) {
        return commentService.getCommentSumByUserId(userId);
    }

    @PostMapping("/add")
    public ResultInfo addComment(@RequestBody Map<String, Object> map) {
        return commentService.addComment(map);
    }

    @DeleteMapping("/{commentId}")
    public ResultInfo deleteComment(@PathVariable("commentId") Long commentId) {
        return commentService.deleteComment(commentId);
    }

    @DeleteMapping("/delete/user/{userId}")
    public ResultInfo deleteCommentByUserId(@PathVariable("userId") Long userId) {
        return commentService.deleteCommentByUserId(userId);
    }

    @DeleteMapping("/delete/article/{articleId}")
    public ResultInfo deleteCommentByArticleId(@PathVariable("articleId") Long articleId) {
        return commentService.deleteCommentByArticleId(articleId);
    }


}


