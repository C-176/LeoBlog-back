package com.chen.LeoBlog.vo.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author <a href="https://github.com/zongzibinbin">abin</a>
 * @since 2023-03-19
 */
@Data
@ApiModel("带有用户ID的基础翻页请求")
public class PageBaseReqWithUserId extends PageBaseReq {

    @ApiModelProperty("用户Id")
    private Long userId;
}
