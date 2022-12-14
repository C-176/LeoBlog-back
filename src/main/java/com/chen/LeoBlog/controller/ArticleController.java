package com.chen.LeoBlog.controller;

import com.chen.LeoBlog.base.ResultInfo;
import com.chen.LeoBlog.service.ArticleService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@CrossOrigin
@RequestMapping("/article")
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    @ApiOperation("获取文章列表")
    @GetMapping("/list/{page}/{size}")
    public ResultInfo getArticleList(@PathVariable("page") Integer page, @PathVariable("size") Integer size) {
        return articleService.getArticleList(page, size);
    }

    @ApiOperation("根据文章id获取文章")
    @GetMapping("/{articleId}")
    public ResultInfo getArticle(@PathVariable("articleId") Long articleId){
        return articleService.getArticle(articleId);
    }
    @PostMapping("/add")
    public ResultInfo addArticle(@RequestBody Map<String, Object> map){
        return articleService.addArticle(map);
    }
    @DeleteMapping("/{articleId}")
    public ResultInfo deleteArticle(@PathVariable("articleId") Long articleId){
        return articleService.deleteArticle(articleId);
    }
    @PutMapping("/update")
    public ResultInfo updateArticle(@RequestBody Map<String, Object> map){

        return articleService.updateArticle(map);
    }
    //获取文章列表
    @GetMapping("/list/user/{userId}/{page}/{size}")
    public ResultInfo getArticleListByUserId(@PathVariable("userId") Long userId,@PathVariable("page") Integer page, @PathVariable("size") Integer size){
        return articleService.getArticleListByUserId(userId,page,size);
    }
    //获取草稿列表
    @GetMapping("/slist/user/{userId}/{page}/{size}")
    public ResultInfo getArticlesListByUserId(@PathVariable("userId") Long userId,@PathVariable("page") Integer page, @PathVariable("size") Integer size){
        return articleService.getArticlesListByUserId(userId,page,size);
    }
    //查询user的文章数
    @GetMapping("/sum/user/{userId}")
    public ResultInfo getArticleSumByUserId(@PathVariable("userId") Long userId){
        return articleService.getArticleSumByUserId(userId);
    }
    //根据分类id获取文章列表
    @GetMapping("/list/sort/{sortId}")
    public ResultInfo getArticleListBySortId(@PathVariable("sortId") Long sortId){
        return articleService.getArticleListBySortId(sortId);
    }
    //根据分类id获取文章数
    @GetMapping("/sum/sort/{sortId}")
    public ResultInfo getArticleSumBySortId(@PathVariable("sortId") Long sortId){
        return articleService.getArticleSumBySortId(sortId);
    }
    //根据标签id获取文章列表
    @PostMapping("/list/label/{labelId}/{page}/{size}")
    public ResultInfo getArticleListByLabelId(@PathVariable("labelId") Long labelId, @RequestBody Map<String,Object> map,@PathVariable("page") Integer page, @PathVariable("size") Integer size){
        return articleService.getArticleListByLabelId(labelId,map,page,size);
    }
    //根据标签id获取文章数
    @GetMapping("/sum/label/{labelId}")
    public ResultInfo getArticleSumByLabelId(@PathVariable("labelId") Long labelId){
        return articleService.getArticleSumByLabelId(labelId);
    }
    //根据关键字获取文章列表
    @GetMapping("/list/keyword/{keyword}/{page}/{size}")
    public ResultInfo getArticleListByKeyword(@PathVariable("keyword") String keyword,@PathVariable("page") Integer page, @PathVariable("size") Integer size){
        log.info("keyword:{}",keyword);
        return articleService.getArticleListByKeyword(keyword,page,size);
    }
    //根据内容获取文章列表
    @GetMapping("/list/content/{content}/{page}/{size}")
    public ResultInfo getArticleListByContent(@PathVariable("content") String content,@PathVariable("page") Integer page, @PathVariable("size") Integer size){
        log.info("content:{}",content);
        return articleService.getArticleListByContent(content,page,size);
    }
    //点赞文章
    @GetMapping("/like/{articleId}")
    public ResultInfo likeArticle(@PathVariable("articleId") String articleId){
        return articleService.likeArticle(Long.parseLong(articleId));
    }
    //收藏文章
    @GetMapping("/collect/{articleId}")
    public ResultInfo collectArticle(@PathVariable("articleId") String articleId){
        return articleService.collectArticle(Long.parseLong(articleId));
    }




}
