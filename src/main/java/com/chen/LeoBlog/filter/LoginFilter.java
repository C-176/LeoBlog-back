package com.chen.LeoBlog.filter;

import cn.hutool.core.util.StrUtil;
import com.chen.LeoBlog.base.SocketPool;
import com.chen.LeoBlog.config.ThreadPoolConfig;
import com.chen.LeoBlog.constant.RedisConstant;
import com.chen.LeoBlog.decorator.HttpServletRequestDecorator;
import com.chen.LeoBlog.exception.HttpErrorEnum;
import com.chen.LeoBlog.po.LoginUser;
import com.chen.LeoBlog.service.impl.UserServiceImpl;
import com.chen.LeoBlog.utils.JWTUtil;
import com.chen.LeoBlog.utils.RedisUtil;
import com.chen.LeoBlog.utils.WebUtil;
import com.chen.LeoBlog.websocket.SocketService;
import com.chen.LeoBlog.websocket.vo.WebSocketData;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
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
import java.util.Date;

import static com.chen.LeoBlog.service.impl.UserServiceImpl.earlyRefresh;

@Component
@Slf4j
public class LoginFilter extends OncePerRequestFilter {
    @Resource
    private RedisUtil redisUtil;
    @Resource
    private SocketService socketService;
    @Resource(name = ThreadPoolConfig.WS_EXECUTOR)
    private ThreadPoolTaskExecutor asyncExecutor;

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        // 获取请求类型
        String method = httpServletRequest.getMethod();

        // 如果是OPTIONS请求则放行
        if ("OPTIONS".equals(method)) {
            filterChain.doFilter(httpServletRequest, httpServletResponse);
            return;
        }

        // 获取token
        String token = httpServletRequest.getHeader("Authorization"), refreshToken = httpServletRequest.getHeader("RefreshAuthorization");
        if (StrUtil.isBlank(token) || StrUtil.isBlank(refreshToken)) {
            filterChain.doFilter(httpServletRequest, httpServletResponse);
            return;
        }
        String userId;
        Date date;
        // 解析token
        try {
            userId = String.valueOf(JWTUtil.parseJwtUserId(token));
            date = JWTUtil.parseJwtExpiration(token);
            Date now = new Date();
            // 提前刷新验证token
            if (now.after(new Date(date.getTime() - earlyRefresh))) {
                httpServletRequest = refreshAccessToken(httpServletRequest, userId);
            }
        } catch (ExpiredJwtException e) {
            try {
                JWTUtil.parseJwtExpiration(refreshToken);
            } catch (Exception exception) {
                log.error("refreshToken 过期");
                WebUtil.responseMsg(httpServletResponse, HttpErrorEnum.UNAUTHORIZED);
                return;
            }
            userId = JWTUtil.parseJwtUserId(refreshToken) + "";
            httpServletRequest = refreshAccessToken(httpServletRequest, userId);
        } catch (Exception e) {
            filterChain.doFilter(httpServletRequest, httpServletResponse);
            return;
        }
        // 获取用户信息
        LoginUser loginUser = redisUtil.getObj(RedisConstant.USER_LOGIN + userId, LoginUser.class);
        if (loginUser == null) {
            WebUtil.responseMsg(httpServletResponse, HttpErrorEnum.UNAUTHORIZED);
            return;
        }
        if (!refreshToken.equals(loginUser.getRefreshToken())) {
            log.error("refreshToken 不一致");
            socketService.sendToSession(socketService.getSessionMap().get(Long.parseLong(userId)), WebSocketData.forceOfflineResponse());
            WebUtil.responseMsg(httpServletResponse, HttpErrorEnum.UNAUTHORIZED);
            return;
        }
        log.debug("用户信息：{}", loginUser);
        // 将用户信息存入到SecurityContext中
        //添加权限
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }

    private HttpServletRequest refreshAccessToken(HttpServletRequest httpServletRequest, String userId) {
        // 1. 生成新的 accessToken
        String accessToken = JWTUtil.generateJwt(userId, UserServiceImpl.accessTokenExpireTime);
        log.info("{},生成新的accessToken：{}", httpServletRequest.getRequestURI(), accessToken);
        // 修改request的header中的accessToken
        //
        HttpServletRequestDecorator httpServletRequestDecorator = new HttpServletRequestDecorator(httpServletRequest);
        httpServletRequestDecorator.setHeader("Authorization", accessToken);
        asyncExecutor.execute(() -> {
            // 调用websocket 推送新的 accessToken
            socketService.sendToSession(SocketPool.getSessionMap().get(Long.parseLong(userId)), WebSocketData.accessToken(accessToken));
        });
        return httpServletRequestDecorator;
    }
}
