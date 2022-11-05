package com.chen.LeoBlog.base;

import com.chen.LeoBlog.dto.UserDto;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


import javax.servlet.http.HttpServlet;

import javax.servlet.http.HttpSession;


@Scope(value= WebApplicationContext.SCOPE_SESSION)

public class Local extends HttpServlet {

//    private static final HttpSession session = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getSession();
//
//    //获取用户信息
//    public static UserDto getUser(){
//        return (UserDto) session.getAttribute("user");
//    }
//
//    public static void saveUser(UserDto userDto) {
//        session.setAttribute("user",userDto);
//    }
//
//    public static void removeUser() {
//        session.removeAttribute("user");
//    }



    private static final ThreadLocal<UserDto> tl = new ThreadLocal<>();

    public static void saveUser(UserDto userDto) {
        tl.set(userDto);
    }

    public static UserDto getUser() {
        return tl.get();
    }

    public static void removeUser() {
        tl.remove();
    }

}
