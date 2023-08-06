package com.chen.LeoBlog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chen.LeoBlog.base.ResultInfo;
import com.chen.LeoBlog.po.Article;
import com.chen.LeoBlog.vo.request.PageBaseReq;
import com.chen.LeoBlog.vo.request.PageBaseReqWithUserId;

import java.util.Map;

/**
 * @author 1
 * @description 针对表【lb_article】的数据库操作Service
 * @createDate 2022-10-17 10:33:52
 */
public interface ArticleService extends IService<Article> {
    ResultInfo getArticleList(PageBaseReq pageBaseReq);

    ResultInfo getArticle(Long articleId);

    ResultInfo addArticle(Map<String, Object> map);

    ResultInfo deleteArticle(Long articleId);

    ResultInfo updateArticle(Map<String, Object> map);

    ResultInfo getArticleListByUserId(PageBaseReqWithUserId pageBaseReq);

    ResultInfo getArticleSumByUserId(Long userId);

    ResultInfo getArticleListBySortId(Long sortId);

    ResultInfo getArticleSumBySortId(Long sortId);

    ResultInfo getArticleListByLabelId(Long labelId, Map<String, Object> map, Integer page, Integer size);

    ResultInfo getArticleSumByLabelId(Long labelId);

    ResultInfo getArticleListByKeyword(String keyword, Integer page, Integer size);

    ResultInfo likeArticle(Long articleId);

    ResultInfo getArticlesListByUserId(PageBaseReq pageBaseReq);

    ResultInfo collectArticle(Long articleId);

    ResultInfo getArticleListByContent(String content, Integer page, Integer size);

    ResultInfo getFollowArticles(int offset, Long lastScore);

}
