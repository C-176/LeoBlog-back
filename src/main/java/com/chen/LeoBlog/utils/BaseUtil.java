package com.chen.LeoBlog.utils;

import com.chen.LeoBlog.base.UserDTOHolder;
import com.chen.LeoBlog.dto.UserDTO;

public class BaseUtil {
    /**
     * 从threadLocal中获取当前登录的用户信息
     *
     * @return 当前登录的用户信息
     */
    public static UserDTO getUserFromLocal() {
        UserDTO user = UserDTOHolder.get();
        AssertUtil.isFalse(user == null, "用户未登录");
        return user;
    }
}
