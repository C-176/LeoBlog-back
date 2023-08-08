package com.chen.LeoBlog.controller;

import com.chen.LeoBlog.annotation.Anonymous;
import com.chen.LeoBlog.base.ResultInfo;
import com.chen.LeoBlog.service.ArticleService;
import com.chen.LeoBlog.vo.request.PageBaseReq;
import com.chen.LeoBlog.vo.request.PageBaseReqWithUserId;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

@RestController
@Slf4j
@CrossOrigin
@RequestMapping("/article")
public class ArticleController {

    @Resource
    private ArticleService articleService;

    @Anonymous
    @PostMapping("/list")
    public ResultInfo getArticleList(@RequestBody PageBaseReq pageBaseReq) {
        return articleService.getArticleList(pageBaseReq);
    }

    @Anonymous
    @GetMapping("/{articleId}")
    public ResultInfo getArticle(@PathVariable("articleId") Long articleId) {
        return articleService.getArticle(articleId);
    }

    @PostMapping("/add")
    public ResultInfo addArticle(@RequestBody Map<String, Object> map) {
        return articleService.addArticle(map);
    }

    @DeleteMapping("/{articleId}")
    public ResultInfo deleteArticle(@PathVariable("articleId") Long articleId) {
        return articleService.deleteArticle(articleId);
    }

    @PutMapping("/update")
    public ResultInfo updateArticle(@RequestBody Map<String, Object> map) {
        return articleService.updateArticle(map);
    }

    //获取文章列表
    @PostMapping("/list/user")
    public ResultInfo<?> getArticleListByUserId(@RequestBody PageBaseReqWithUserId pageBaseReq) {
        return articleService.getArticleListByUserId(pageBaseReq);
    }

    //获取草稿列表
    @PostMapping("/slist/user")
    public ResultInfo getArticlesListByUserId(@RequestBody PageBaseReq pageBaseReq) {
        return articleService.getArticlesListByUserId(pageBaseReq);
    }

    //查询user的文章数
    @GetMapping("/sum/user/{userId}")
    public ResultInfo getArticleSumByUserId(@PathVariable("userId") Long userId) {
        return articleService.getArticleSumByUserId(userId);
    }

    //查询关注列表中的新发文章
    @GetMapping("/follow/{offset}/{lastScore}")
    public ResultInfo getFollowArticles(@PathVariable("offset") int offset, @PathVariable("lastScore") Long lastScore) {
        return articleService.getFollowArticles(offset, lastScore);
    }

    @Anonymous
    @GetMapping("/sum/sort/{sortId}")
    public ResultInfo getArticleSumBySortId(@PathVariable("sortId") Long sortId) {
        return articleService.getArticleSumBySortId(sortId);
    }

    //根据分类id获取文章列表
    @Anonymous
    @GetMapping("/list/sort/{sortId}")
    public ResultInfo getArticleListBySortId(@PathVariable("sortId") Long sortId) {
        return articleService.getArticleListBySortId(sortId);
    }

    //根据标签id获取文章列表

    @GetMapping("/list/label/{labelId}/{page}/{size}")
    public ResultInfo getArticleListByLabelId(@PathVariable("labelId") Long labelId, @RequestBody Map<String, Object> map, @PathVariable("page") Integer page, @PathVariable("size") Integer size) {
        return articleService.getArticleListByLabelId(labelId, map, page, size);
    }

    //根据标签id获取文章数
    @Anonymous
    @GetMapping("/sum/label/{labelId}")
    public ResultInfo getArticleSumByLabelId(@PathVariable("labelId") Long labelId) {
        return articleService.getArticleSumByLabelId(labelId);
    }

    //根据关键字获取文章列表
    @Anonymous
    @GetMapping("/list/keyword/{keyword}/{page}/{size}")
    public ResultInfo getArticleListByKeyword(@PathVariable("keyword") String keyword, @PathVariable("page") Integer page, @PathVariable("size") Integer size) {
        log.info("keyword:{}", keyword);
        return articleService.getArticleListByKeyword(keyword, page, size);
    }

    //根据内容获取文章列表
    @Anonymous
    @GetMapping("/list/content/{content}/{page}/{size}")
    public ResultInfo getArticleListByContent(@PathVariable("content") String content, @PathVariable("page") Integer page, @PathVariable("size") Integer size) {
        log.info("content:{}", content);
        return articleService.getArticleListByContent(content, page, size);
    }

    //点赞文章
    @PutMapping("/like/{articleId}")
    public ResultInfo likeArticle(@PathVariable("articleId") Long articleId) {
        return articleService.likeArticle(articleId);
    }

    //收藏文章
    @PutMapping("/collect/{articleId}")
    public ResultInfo collectArticle(@PathVariable("articleId") Long articleId) {
        log.info("articleId:{}", articleId);
        log.info("articleId:{}", articleId);
        return articleService.collectArticle(articleId);
    }


}
