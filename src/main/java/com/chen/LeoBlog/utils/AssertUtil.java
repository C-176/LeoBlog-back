package com.chen.LeoBlog.utils;

import com.chen.LeoBlog.exception.ParamsException;

public class AssertUtil {

    // 判断是否为真，为真的话抛出参数异常msg。

    public static void isTrue(Boolean flag, String msg) {
        if(flag) {
            throw new ParamsException(msg);
        }
    }

}
