package com.chen.LeoBlog.activityEvent;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@ApiModel(value = "活动事件")
@NoArgsConstructor
@AllArgsConstructor
public class ActivityEvent {
    @ApiModelProperty(value = "活动事件id")
    private Integer id;
    @ApiModelProperty(value = "活动事件类型")
    private Integer type;
    @ApiModelProperty(value = "活动事件发起者id")
    private Long userId;
    @ApiModelProperty(value = "活动事件接收者id")
    private Long targetId;
    @ApiModelProperty(value = "活动事件数据")
    private EventData eventData;
    @ApiModelProperty(value = "活动事件路由")
    private String router;
    @ApiModelProperty(value = "活动事件创建时间")
    private Date createTime;
}
