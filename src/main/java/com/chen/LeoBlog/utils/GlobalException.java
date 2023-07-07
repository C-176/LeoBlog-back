package com.chen.LeoBlog.utils;

import cn.hutool.json.JSONUtil;
import com.chen.LeoBlog.base.ResultInfo;
import com.chen.LeoBlog.exception.NoLoginException;
import com.chen.LeoBlog.exception.ParamsException;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

//@Component
public class GlobalException implements HandlerExceptionResolver {

    @Override
    public ModelAndView resolveException(HttpServletRequest req, HttpServletResponse resp, Object handler, Exception e) {
        //未登录异常
        if (e instanceof NoLoginException) {
            return new ModelAndView("redirect:index.html");
        }
        //实例化对象
        ModelAndView mav = new ModelAndView();
        //默认视图
        mav.setViewName("");
        mav.addObject("code", 400);
        mav.addObject("msg", "系统异常，请稍后访问...");
        //判断ResponseBody
        if (handler instanceof HandlerMethod handlerMethod) {
            ResponseBody responseBody = handlerMethod.getMethod().getDeclaredAnnotation(ResponseBody.class);
            //判断
            if (responseBody == null) {
                //返回试图
                if (e instanceof ParamsException) {
                    ParamsException pe = (ParamsException) e;
                    mav.addObject("code", pe.getCode());
                    mav.addObject("msg", pe.getData());
                }
                return mav;
            } else {
                System.out.println(e.getMessage());
                //返回的json数据
                ResultInfo resultInfo = new ResultInfo();
                resultInfo.setMsg("系统异常");
                resultInfo.setCode(400);
                if (e instanceof ParamsException) {
                    ParamsException pe = (ParamsException) e;
                    resultInfo.setCode(pe.getCode());
                    resultInfo.setMsg(pe.getData());
                }
                //resp
                resp.setContentType("application/json;charset=utf-8");
                //获取输入流
                PrintWriter out = null;
                try {
                    out = resp.getWriter();
                    //输出
                    out.write(JSONUtil.toJsonStr(resultInfo));
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                } finally {
                    if (out != null) {
                        out.flush();
                        out.close();
                    }
                }
            }
        }
        return mav;
    }
}
