package com.chen.LeoBlog.filter;

import cn.hutool.core.lang.UUID;
import com.chen.LeoBlog.constant.MDCKey;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@WebFilter(urlPatterns = "/*")
public class HttpTraceIdFilter extends OncePerRequestFilter {


    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        String tid = UUID.randomUUID().toString();
        // MDC底层是个threadLocal，所以不用担心并发问题，但是如果是异步线程，需要手动传递
        //TODO：了解MDC
        MDC.put(MDCKey.TID, tid);
        filterChain.doFilter(httpServletRequest, httpServletResponse);
        MDC.remove(MDCKey.TID);
    }
}