package com.chen.LeoBlog.utils;

import cn.hutool.core.util.StrUtil;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class CookieUtil {

    public static void setCookie(String key, String value, String domain,
                                 HttpServletResponse response) {

        try {
            value = URLEncoder.encode(value, "UTF-8");
            if (StrUtil.isNotBlank(value)) {
                value = value.replaceAll("\\+", "%20");
            }
            Cookie cookie = new Cookie(key, value);
            cookie.setMaxAge(-1);
            cookie.setPath("/");
            cookie.setDomain(domain);
            response.addCookie(cookie);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置Cookie
     *
     * @param key      Cookie名称
     * @param value    Cookie Value
     */
    public static void setCookieNoEncode(String key, String value, String domain,
                                         HttpServletResponse response) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(-1);
        cookie.setPath("/");
        cookie.setDomain(domain);
        response.addCookie(cookie);
    }

    /**
     * 获取Cookie
     *
     * @param request
     * @param key
     * @return
     */
    public static String getCookieValue(HttpServletRequest request, String key) {
        Cookie[] cookies = request.getCookies();
        Cookie cookie = null;
        if (cookies != null) {
            for (int i = 0; i < cookies.length; i++) {
                if (cookies[i].getName().equals(key)) {
                    cookie = cookies[i];
                    break;
                }
            }
        }
        if (cookie != null) {
            return URLDecoder.decode(cookie.getValue(), StandardCharsets.UTF_8);
        }
        return null;
    }

    /**
     * 清除cookie
     *
     * @param cookieName
     * @param request
     * @param response
     */
    public static void deleteCookie(String cookieName, HttpServletRequest request, HttpServletResponse response) {
        Cookie[] arrCookie = request.getCookies();
        if (arrCookie != null && arrCookie.length > 0) {
            for (Cookie cookie : arrCookie) {
                if (cookie.getName().equals(cookieName)) {
                    cookie.setValue("");
                    cookie.setMaxAge(0);
                    cookie.setPath("/");
                    response.addCookie(cookie);
                }
            }
        }
    }
}
