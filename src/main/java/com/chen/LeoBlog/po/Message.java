package com.chen.LeoBlog.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName lb_message
 */
@TableName(value ="lb_message")
@Data
public class Message implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
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
     * 0：普通，-1为全体消息。
     */
    private Integer messageType;

    /**
     * 
     */
    private String messageRedirect;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}