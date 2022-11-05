package com.chen.LeoBlog.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
 * 
 * @TableName lb_comment
 */
@TableName(value ="lb_comment")
@Data
public class Comment implements Serializable {
    /**
     * 评论id
     */
    @TableId(type = IdType.AUTO)
    private Long commentId;

    /**
     * 评论者id
     */
    private Long userId;

    /**
     * 评论文章id
     */
    private Long articleId;

    /**
     * 内容
     */
    private String commentContent;

    /**
     * 评论日期
     */
    private Date commentUpdateTime;

    /**
     * 被回复的评论的id
     */
    private Long commentParentId;

    /**
     * 点赞数
     */
    private Long commentLikes;

    /**
     * 
     */
    private Long receiverId;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}