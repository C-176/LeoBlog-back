package com.chen.LeoBlog.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 
 * @TableName lb_friend
 */
@TableName(value ="lb_friend")
@Data
public class Friend implements Serializable {
    /**
     * 标识ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 好友ID
     */
    private Long userFriendId;

    /**
     * 好友备注
     */
    private String userNote;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}