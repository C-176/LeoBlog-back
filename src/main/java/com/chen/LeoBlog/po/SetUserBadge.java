package com.chen.LeoBlog.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户徽章对应表
 * @TableName lb_set_user_badge
 */
@TableName(value ="lb_set_user_badge")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SetUserBadge implements Serializable {
    /**
     * 
     */
    private Long userId;

    /**
     * 
     */
    private Long badgeId;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}