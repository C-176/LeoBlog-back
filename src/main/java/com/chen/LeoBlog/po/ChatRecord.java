package com.chen.LeoBlog.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * @TableName lb_chat_record
 */
@TableName(value = "lb_chat_record")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRecord implements Serializable {
    /**
     *
     */
    @TableId(type = IdType.AUTO)
    private Long recordId;

    /**
     * 发送者id
     */
    private Long userId;

    /**
     * 接收者id
     */
    private Long receiverId;

    /**
     * 消息内容
     */
    private String recordContent;

    /**
     * 消息发送时间
     */
    private Date recordUpdateTime;

    /**
     *
     */
    private Integer isSaw;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    public ChatRecord(Long userId, Long receiverId, String recordContent, Date recordUpdateTime) {
        this.userId = userId;
        this.receiverId = receiverId;
        this.recordContent = recordContent;
        this.recordUpdateTime = recordUpdateTime;
        this.isSaw = 0;
    }


}