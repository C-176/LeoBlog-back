package com.chen.LeoBlog.utils;

import cn.hutool.json.JSONUtil;
import com.chen.LeoBlog.base.ResultInfo;
import com.chen.LeoBlog.enums.ErrorEnum;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@Slf4j
public class WebUtil {

    public static void responseMsg(HttpServletResponse response, Integer code, String msg) throws IOException {
        response.setStatus(200);
        response.setContentType("application/json;charset=UTF-8");
        if (response.getHeader("Access-Control-Allow-Origin") == null) {
            response.setHeader("Access-Control-Allow-Origin", "*");
        }
        response.setHeader("Access-Control-Allow-Headers", "Content-Type,Content-Length, Authorization, Accept,X-Requested-With");
        //设置编码格式
        response.setCharacterEncoding("UTF-8");
        response.getWriter().print(JSONUtil.toJsonStr(ResultInfo.fail(code, msg)));
    }

    public static void responseMsg(HttpServletResponse response, ErrorEnum errorEnum) throws IOException {
        response.setStatus(200);
        response.setContentType("application/json;charset=UTF-8");
        if (response.getHeader("Access-Control-Allow-Origin") == null) {
            response.setHeader("Access-Control-Allow-Origin", "*");
        }
        response.setHeader("Access-Control-Allow-Headers", "Content-Type,Content-Length, Authorization, Accept,X-Requested-With");
        //设置编码格式
        response.setCharacterEncoding("UTF-8");
        response.getWriter().print(JSONUtil.toJsonStr(ResultInfo.fail(errorEnum.getErrorCode(), errorEnum.getErrorMsg())));
    }
}
