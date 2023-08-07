package com.chen.LeoBlog.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.chen.LeoBlog.enums.MsgTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * @TableName lb_message
 */
@TableName(value = "lb_message")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Message implements Serializable {
    /**
     *
     */
    @TableId
    private Long messageId;

    /**
     *
     */
    private Long userId;

    /**
     *
     */
    private Long receiverId;

    /**
     *
     */
    private String messageTitle;

    /**
     *
     */
    private String messageContent;

    /**
     *
     */
    private Date messageUpdateTime;

    /**
     *
     */
    private Integer isSaw;

    /**
     * type:0-发表文章 1-评论文章 2-收藏文章 3-点赞文章
     * 4-关注用户 5-回复评论 6-系统消息
     */
    private Integer messageType;

    private String messageRedirect;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    public Message(Long messageId, Long userId, Long receiverId, String messageTitle, String messageRedirect) {
        this.messageId = messageId;
        this.userId = userId;
        this.receiverId = receiverId;
        this.messageTitle = messageTitle;
        this.messageRedirect = messageRedirect;
    }

    public Message(Long messageId, Long userId, Long receiverId, String messageTitle, MsgTypeEnum messageType, String messageRedirect) {
        this.messageId = messageId;
        this.userId = userId;
        this.receiverId = receiverId;
        this.messageTitle = messageTitle;
        this.messageType = messageType.getCode();
        this.messageRedirect = messageRedirect;
        this.messageUpdateTime = new Date();
    }
}