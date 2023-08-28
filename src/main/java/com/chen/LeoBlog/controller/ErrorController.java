package com.chen.LeoBlog.controller;

import com.chen.LeoBlog.exception.BusinessException;
import com.chen.LeoBlog.exception.HttpErrorEnum;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public class ErrorController extends BasicErrorController {


    public ErrorController(ServerProperties serverProperties) {
        super(new DefaultErrorAttributes(), serverProperties.getError());
    }

    /**
     * 覆盖默认的Json响应
     */
    @Override
    public ResponseEntity<Map<String, Object>> error(HttpServletRequest request) {
        throw new BusinessException(HttpErrorEnum.BAD_REQUEST);
    }


}
