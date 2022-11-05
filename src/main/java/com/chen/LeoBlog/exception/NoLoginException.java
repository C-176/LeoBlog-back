package com.chen.LeoBlog.exception;

import lombok.Data;

/**
 * 自定义参数异常
 */
@Data
public class NoLoginException extends RuntimeException {
    private Integer code = 401;
    private String msg = "用户未登录!";
    private Object data = msg;


    public NoLoginException() {
        super("用户未登录!");
    }

}
