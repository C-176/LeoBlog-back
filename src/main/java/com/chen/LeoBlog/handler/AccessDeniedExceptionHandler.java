package com.chen.LeoBlog.handler;

import com.chen.LeoBlog.base.ResultInfo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class AccessDeniedExceptionHandler {

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseBody
    public ResultInfo<String> handleAccessDeniedException() {
        return ResultInfo.fail(HttpStatus.FORBIDDEN.value(), "权限不足，访问被拒绝");
    }
}
