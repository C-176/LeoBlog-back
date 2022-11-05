package com.chen.LeoBlog.exception;

/**
 * 自定义参数异常
 */
public class ParamsException extends RuntimeException {
    private Integer code = 300;
    private String data = "参数异常!";


    public ParamsException() {
        super("参数异常!");
    }

    public ParamsException(String data) {
        super(data);
        this.data = data;
    }

    public ParamsException(Integer code) {
        super("参数异常!");
        this.code = code;
    }

    public ParamsException(Integer code, String data) {
        super(data);
        this.code = code;
        this.data = data;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
