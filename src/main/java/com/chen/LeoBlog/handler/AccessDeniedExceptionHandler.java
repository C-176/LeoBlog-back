package com.chen.LeoBlog.handler;

import com.chen.LeoBlog.base.ResultInfo;
import com.chen.LeoBlog.exception.HttpErrorEnum;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class AccessDeniedExceptionHandler {

    @ExceptionHandler({AccessDeniedException.class})
    public ResultInfo<String> handleAccessDeniedException() {
        return ResultInfo.fail(HttpErrorEnum.FORBIDDEN);
    }
}
