package com.chen.LeoBlog.utils;

import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
public class PathUtil {
    /**
     * 返回当前项目的绝对路径：D:/JavaCode/springboot/src/main/resources/static/source/upload/
     * @param request
     * @return
     */
    public static String getUploadPath(HttpServletRequest request) {
        //        对于springboot来说获取根目录,获得的是与src同一级的public目录
        String realPath = request.getServletContext().getRealPath("/");
//        System.out.println(realPath);
//        处理realPath,找到最后要存文件的地方。
        realPath = realPath.substring(0, realPath.substring(0, realPath.lastIndexOf("\\")).lastIndexOf("\\")) + "\\src\\main\\resources\\static\\source\\upload\\";
        return realPath;
    }
}
