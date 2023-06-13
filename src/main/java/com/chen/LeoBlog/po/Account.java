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
 * @TableName lb_account
 */
@TableName(value ="lb_account")
@Data
public class Account implements Serializable {
    /**
     * 用户id
     */
    @TableId
    private Long userId;

    /**
     * 用户金币数
     */
    private Long userMoney;

    /**
     * 更新金币的时间
     */
    private Date updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}