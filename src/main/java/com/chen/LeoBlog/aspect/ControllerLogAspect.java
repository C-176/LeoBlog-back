package com.chen.LeoBlog.aspect;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.chen.LeoBlog.annotation.Anonymous;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Aspect
@Slf4j
@Component
public class ControllerLogAspect {

    @Around("execution(* com.chen.LeoBlog.controller.*.*(..))")
    public Object controllerLog(ProceedingJoinPoint joinPoint) throws Throwable {
        // 判断方法是否有@Anonymous注解，有则直接放行
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        boolean isAnonymous = signature.getMethod().isAnnotationPresent(Anonymous.class);
        if (isAnonymous) return joinPoint.proceed();
        // 获取request参数，打印请求参数
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        String method = request.getMethod();
        String uri = request.getRequestURI();
        //如果参数有HttpRequest,ServletResponse，直接移除，不打印这些
        List<Object> paramList = Stream.of(joinPoint.getArgs()).filter(args -> !(args instanceof ServletRequest)).filter(args -> !(args instanceof ServletResponse)).collect(Collectors.toList());
        String printParamStr = paramList.size() == 1 ? JSONUtil.toJsonStr(paramList.get(0) + "") : JSONUtil.toJsonStr(paramList);
        if (log.isInfoEnabled()) {
            log.info("[{}:{}]" + (paramList.size() == 0 ? "" : StrUtil.format("|request:{}", printParamStr)), method, uri);
        }
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Object result = joinPoint.proceed();
        stopWatch.stop();
        long cost = stopWatch.getTotalTimeMillis();
        String printResultStr = JSONUtil.toJsonStr(result);
        if (log.isInfoEnabled()) {
            log.info("[{}:{}]->cost:{}ms", method, uri, cost);
        }
        return result;
    }
}
