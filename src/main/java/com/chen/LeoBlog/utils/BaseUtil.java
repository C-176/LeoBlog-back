package com.chen.LeoBlog.utils;

import com.chen.LeoBlog.base.Local;
import com.chen.LeoBlog.dto.UserDto;

public class BaseUtil {
    public static UserDto getUserFromLocal() {
        UserDto user = Local.getUser();
        AssertUtil.isTrue(user == null, "用户未登录");
        return user;

    }
}
