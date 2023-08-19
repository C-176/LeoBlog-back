package com.chen.LeoBlog.filter;

import cn.hutool.core.lang.UUID;
import com.chen.LeoBlog.constant.MDCKey;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import java.io.IOException;

@Slf4j
@WebFilter(urlPatterns = "/*")
public class HttpTraceIdFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String tid = UUID.randomUUID().toString();
        MDC.put(MDCKey.TID, tid);
        chain.doFilter(request, response);
    }

}