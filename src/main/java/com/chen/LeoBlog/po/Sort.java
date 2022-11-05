package com.chen.LeoBlog.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 
 * @TableName lb_sort
 */
@TableName(value ="lb_sort")
@Data
public class Sort implements Serializable {
    /**
     * 分类ID
     */
    @TableId
    private Long sortId;

    /**
     * 分类名称
     */
    private String sortName;

    /**
     * 分类别名
     */
    private String sortAlias;

    /**
     * 分类描述
     */
    private String sortDescription;

    /**
     * 父分类ID
     */
    private Long parentSortId;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}