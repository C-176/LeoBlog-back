package com.chen.LeoBlog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chen.LeoBlog.po.User;

import java.util.Set;

/**
 * @author 1
 * @description 针对表【lb_user】的数据库操作Mapper
 * @createDate 2022-10-29 10:05:08
 * @Entity com.chen.LeoBlog.po.User
 */
public interface UserMapper extends BaseMapper<User> {

    Set<String> getPermissions(Long userId);

    Integer getRoleByUserId(Long userId);
}




