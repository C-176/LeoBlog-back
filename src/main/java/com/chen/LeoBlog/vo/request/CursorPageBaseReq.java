package com.chen.LeoBlog.vo.request;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

/**
 * @author <a href="https://github.com/zongzibinbin">abin</a>
 * @since 2023-03-19
 */
@Data
@ApiModel("游标翻页请求")
@AllArgsConstructor
@NoArgsConstructor
public class CursorPageBaseReq {

    @ApiModelProperty("页面大小")
    @Min(0)
    @Max(100)
    private Integer pageSize = 10;

    @ApiModelProperty("偏移量")
    private Integer offset = 0;

    @ApiModelProperty("游标（初始为null，后续请求附带上次翻页的游标）")
    private String cursor;


    public <T> Page<T> plusPage() {
        return new Page<>(1, this.pageSize);
    }

    @JsonIgnore
    public Boolean isFirstPage() {
        return StrUtil.isEmpty(cursor);
    }
}
