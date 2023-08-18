package com.chen.LeoBlog.decorator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.web.firewall.FirewalledRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Slf4j
public class HttpServletRequestDecorator extends FirewalledRequest {

    public HttpServletRequestDecorator(HttpServletRequest request) {
        super(request);
    }

    @Override
    public void reset() {
        headerMap.clear();
    }

    private final Map<String, String> headerMap = new ConcurrentHashMap<>();

    @Override
    public String getHeader(String name) {
        if (headerMap.containsKey(name)) {
            return headerMap.get(name);
        }
        return super.getHeader(name);
    }

    public void setHeader(String name, String value) {
        headerMap.put(name, value);
    }
}