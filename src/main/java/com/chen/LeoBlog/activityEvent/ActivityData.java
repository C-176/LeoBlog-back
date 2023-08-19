package com.chen.LeoBlog.activityEvent;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "活动事件数据")
public class ActivityData {
    @ApiModelProperty(value = "文章id")
    private Long articleId;
    @ApiModelProperty(value = "评论id")
    private Long commentId;
    @ApiModelProperty(value = "用户id")
    private Long userId;
    @ApiModelProperty(value = "文章标题")
    private String articleTitle;
    @ApiModelProperty(value = "评论内容")
    private String commentContent;
    @ApiModelProperty(value = "用户名称")
    private String userName;
    @ApiModelProperty(value = "徽章id")
    private Long badgeId;
    @ApiModelProperty(value = "徽章名称")
    private String badgeName;

}
