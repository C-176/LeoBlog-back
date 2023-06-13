package com.chen.LeoBlog.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @TableName lb_article
 */
@TableName(value ="lb_article")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Article implements Serializable {
    /**
     * 文章id
     */
    @TableId
    private Long articleId;

    /**
     * 
     */
    private Integer isArticle;

    /**
     * 发表用户id
     */
    private Long userId;

    /**
     * 文章标题
     */
    private String articleTitle;

    /**
     * 文章内容
     */
    private String articleContent;

    /**
     * 发表时间
     */
    private Date articleUpdateDate;

    /**
     * 文章主背景图
     */
    private String articlePic;

    /**
     * 浏览量
     */
    private Long articleViews;

    /**
     * 评论总数
     */
    private Long articleComments;

    /**
     * 点赞数
     */
    private Long articleLikes;

    /**
     * 收藏数
     */
    private Long articleCollects;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    public Article(Integer isArticle, Long userId, String articleTitle, String articleContent, Date articleUpdateDate) {
        this.isArticle = isArticle;
        this.userId = userId;
        this.articleTitle = articleTitle;
        this.articleContent = articleContent;
        this.articleUpdateDate = articleUpdateDate;
    }

}