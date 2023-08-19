package com.chen.LeoBlog.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * @TableName lb_account
 */
@TableName(value = "lb_account")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "Account", description = "账户信息")
public class Account implements Serializable {
    /**
     * 用户id
     */
    @TableId
    @ApiModelProperty(value = "用户id")
    private Long userId;

    /**
     * 用户金币数
     */
    @ApiModelProperty(value = "用户金币数")
    private Long userMoney;

    /**
     * 更新金币的时间
     */
    @ApiModelProperty(value = "更新金币的时间")
    private Date updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}