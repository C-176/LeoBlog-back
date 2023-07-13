package com.chen.LeoBlog.vo.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("聊天记录游标翻页请求")
public class ChatCursorPageBaseReq extends CursorPageBaseReq {

    @ApiModelProperty("聊天对象id")
    private String talkToId;
}
