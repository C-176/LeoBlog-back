package com.chen.LeoBlog.exception;

import com.chen.LeoBlog.enums.ErrorEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Description: 业务校验异常码
 */
@AllArgsConstructor
@Getter
public enum HttpErrorEnum implements ErrorEnum {
    UNAUTHORIZED(401, "登录失效，请重新登录"),
    FORBIDDEN(403, "权限不足，禁止访问"),
    NOT_FOUND(404, "请求资源不存在，请重试"),
    SERVER_ERROR(500, "服务器异常"),
    ;
    private Integer httpCode;
    private String msg;

    @Override
    public Integer getErrorCode() {
        return httpCode;
    }

    @Override
    public String getErrorMsg() {
        return msg;
    }


}
