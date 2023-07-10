package com.chen.LeoBlog.base;

import com.chen.LeoBlog.dto.UserDTO;


import javax.servlet.http.HttpServlet;


public class Local extends HttpServlet {
    private static final ThreadLocal<UserDTO> tl = new ThreadLocal<>();

    public static void saveUser(UserDTO userDto) {
        tl.set(userDto);
    }

    public static UserDTO getUser() {
        return tl.get();
    }

    public static void removeUser() {
        tl.remove();
    }

}
