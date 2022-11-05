package com.chen.LeoBlog.constant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class BaseConstant {
    @Value("${server.port}")
    private static int port;

    public static final String PATH_PREFIX = "http://localhost:"+port+"/";
}
