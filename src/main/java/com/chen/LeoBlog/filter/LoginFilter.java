package com.chen.LeoBlog.filter;

import cn.hutool.core.util.StrUtil;
import com.chen.LeoBlog.constant.RedisConstant;

import com.chen.LeoBlog.po.LoginUser;
import com.chen.LeoBlog.utils.JWTUtil;
import com.chen.LeoBlog.utils.RedisUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class LoginFilter extends OncePerRequestFilter {
    @Resource
    private RedisUtil redisUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        // 获取token
        String token = httpServletRequest.getHeader("Authorization");
        if (StrUtil.isBlank(token)) {
            filterChain.doFilter(httpServletRequest, httpServletResponse);
            return;
        }
        Jws<Claims> claimsJws;
        // 解析token
        try {
            claimsJws = JWTUtil.parseJwt(token);
//            if (!JWTUtil.verifyJwtSignature(token)) throw new RuntimeException("token非法");
        } catch (Exception e) {
            logger.error(e);
            throw new RuntimeException("token非法");
        }
        String userId = claimsJws.getBody().getSubject();

        // 获取用户信息
        LoginUser loginUser = redisUtil.getObj(RedisConstant.USER_LOGIN + userId, LoginUser.class);
        if (loginUser == null) throw new RuntimeException("token过期,请重新登录");
        // 将用户信息存入到SecurityContext中
        //添加权限
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        filterChain.doFilter(httpServletRequest, httpServletResponse);
        // 放行

    }
}
