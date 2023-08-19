package com.chen.LeoBlog.vo.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("聊天记录游标翻页请求")
public class CursorPageBaseReqWithUserId extends CursorPageBaseReq {

    @ApiModelProperty("userId")
    private Long userId;
}
