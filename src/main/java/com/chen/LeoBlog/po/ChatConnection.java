package com.chen.LeoBlog.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 聊天对象列表
 * @TableName lb_chat_connection
 */
@TableName(value ="lb_chat_connection")
@Data
public class ChatConnection implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 聊天对象id
     */
    private Long chatUserId;

    /**
     * 最后一次聊天时间
     */
    private Date chatLastTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}