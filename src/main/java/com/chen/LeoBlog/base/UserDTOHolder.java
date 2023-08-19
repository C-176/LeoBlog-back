package com.chen.LeoBlog.base;

import com.chen.LeoBlog.dto.UserDTO;

import javax.servlet.http.HttpServlet;


public class UserDTOHolder extends HttpServlet {
    private static final ThreadLocal<UserDTO> userDTOHolder = new ThreadLocal<>();

    public static void set(UserDTO userDto) {
        userDTOHolder.set(userDto);
    }

    public static UserDTO get() {
        return userDTOHolder.get();
    }

    public static void remove() {
        userDTOHolder.remove();
    }

}
