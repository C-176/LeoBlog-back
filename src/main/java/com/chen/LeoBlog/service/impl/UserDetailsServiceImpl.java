package com.chen.LeoBlog.service.impl;

import com.chen.LeoBlog.mapper.UserMapper;
import com.chen.LeoBlog.po.LoginUser;
import com.chen.LeoBlog.po.User;
import com.chen.LeoBlog.service.UserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;

@Service
/**
 * 默认的UserDetailsService实现类的逻辑是在内存中查询用户信息
 * 为了实现从数据库中查询用户信息，需要自定义UserDetailsService实现类
 */
public class UserDetailsServiceImpl implements UserDetailsService {
    @Resource
    private UserService userService;

    @Resource
    private UserMapper userMapper;


    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        if (s == null || "".equals(s)) throw new UsernameNotFoundException("用户名不能为空");
        User user;
        if (s.contains("@")) user = userService.query().eq("user_email", s).one();
        else user = userService.query().eq("user_name", s).one();
        if (user == null) throw new UsernameNotFoundException("用户不存在");


        //TODO:查询用户的权限信息
        Set<String> permissions = userMapper.getPermissions(user.getUserId());
        // 返回UserDetails的实现类
        return new LoginUser(user, permissions);

    }
}
