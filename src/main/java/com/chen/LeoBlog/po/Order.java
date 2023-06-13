package com.chen.LeoBlog.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 
 * @TableName lb_order
 */
@TableName(value ="lb_order")
@Data
@AllArgsConstructor
public class Order implements Serializable {
    /**
     * 订单id
     */
    @TableId
    private Long orderId;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 徽章id
     */
    private Long badageId;

    /**
     * 订单生成时间
     */
    private Date orderCreateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}