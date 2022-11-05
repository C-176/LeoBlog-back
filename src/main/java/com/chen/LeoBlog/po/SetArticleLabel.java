package com.chen.LeoBlog.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 
 * @TableName lb_set_article_label
 */
@TableName(value ="lb_set_article_label")
@Data
public class SetArticleLabel implements Serializable {
    /**
     * 文章id
     */

    private Long articleId;

    /**
     * 对应的标签id
     */
    private Long labelId;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}