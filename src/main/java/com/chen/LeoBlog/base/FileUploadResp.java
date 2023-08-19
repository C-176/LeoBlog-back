package com.chen.LeoBlog.base;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("文件上传返回")
public class FileUploadResp {
    @ApiModelProperty("0-成功 1-失败")
    private Integer error;
    @ApiModelProperty("文件地址")
    private String url;
    @ApiModelProperty("错误信息")
    private String message;

    public static FileUploadResp success(String url) {
        return new FileUploadResp(0, url, null);
    }

    public static FileUploadResp fail(String message) {
        return new FileUploadResp(1, null, message);
    }
}
