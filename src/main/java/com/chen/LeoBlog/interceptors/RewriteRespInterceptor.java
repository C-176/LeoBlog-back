package com.chen.LeoBlog.interceptors;

import com.chen.LeoBlog.exception.HttpErrorEnum;
import com.chen.LeoBlog.utils.WebUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@Slf4j
@Component
public class RewriteRespInterceptor implements HandlerInterceptor {

    @Override
    public synchronized boolean preHandle(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response, Object handler) throws IOException {
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        int status = response.getStatus();
        switch (status) {
            case HttpServletResponse.SC_BAD_REQUEST, HttpServletResponse.SC_NOT_FOUND ->
                    WebUtil.responseMsg(response, HttpErrorEnum.BAD_REQUEST);
            case HttpServletResponse.SC_UNAUTHORIZED -> WebUtil.responseMsg(response, HttpErrorEnum.UNAUTHORIZED);
            case HttpServletResponse.SC_FORBIDDEN -> WebUtil.responseMsg(response, HttpErrorEnum.FORBIDDEN);
            case HttpServletResponse.SC_INTERNAL_SERVER_ERROR ->
                    WebUtil.responseMsg(response, HttpErrorEnum.SERVER_ERROR);
            case HttpServletResponse.SC_BAD_GATEWAY -> WebUtil.responseMsg(response, HttpErrorEnum.GATEWAY_ERROR);
        }


    }


}

