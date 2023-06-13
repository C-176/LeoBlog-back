package com.chen.LeoBlog.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 
 * @TableName lb_badge
 */
@TableName(value ="lb_badge")
@Data
public class Badge implements Serializable {
    /**
     * 徽章id
     */
    @TableId(type = IdType.AUTO)
    private Long badgeId;

    /**
     * 徽章名字
     */
    private String badgeName;

    /**
     * 徽章描述
     */
    private String badgeDescription;

    /**
     * 徽章剩余库存
     */
    private Integer badgeStock;

    /**
     * 0:普通徽章 1：限量徽章
     */
    private Integer badgeType;

    /**
     * 徽章的价值
     */
    private Integer badgeValue;

    /**
     * 徽章图片链接
     */
    private String badgeIcon;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}