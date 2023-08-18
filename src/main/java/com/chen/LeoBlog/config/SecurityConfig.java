package com.chen.LeoBlog.config;

import com.chen.LeoBlog.annotation.Anonymous;
import com.chen.LeoBlog.filter.LoginFilter;
import com.chen.LeoBlog.handler.SecurityExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Set;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Resource
    private LoginFilter loginFilter;

    @Resource
    private SecurityExceptionHandler securityExceptionHandler;
//    // 修改默认的密码加密方式
//     @Bean
//     public PasswordEncoder passwordEncoder() {
//         return new BCryptPasswordEncoder();
//     }

    // 暴露AuthenticationManager到spring容器中
    @Bean("authenticationManager")
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }


    @Override

    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and().authorizeRequests()
                .antMatchers("/source/**", "/v2/**", "/favicon.ico").permitAll().anyRequest().authenticated();
        http.addFilterBefore(loginFilter, UsernamePasswordAuthenticationFilter.class);
        // 添加验证失败和鉴权失败异常处理器，统一响应格式
        http.exceptionHandling()
                .authenticationEntryPoint(securityExceptionHandler)
                .accessDeniedHandler(securityExceptionHandler);
        // 允许跨域
        http.cors();


        //判断是否有匿名注解
        Map<RequestMappingInfo, HandlerMethod> handlerMethods = requestMappingHandlerMapping.getHandlerMethods();
        handlerMethods.forEach((info, method) -> {
            boolean isAnonymous = method.getMethod().isAnnotationPresent(Anonymous.class);
            // 带Anonymous注解的方法直接放行
            if (!isAnonymous) isAnonymous = method.getBeanType().isAnnotationPresent(Anonymous.class);
            if (!isAnonymous) return;
            // 根据请求类型做不同的处理
            info.getMethodsCondition().getMethods().forEach(requestMethod -> {
                if (requestMethod == RequestMethod.GET) {
                    // getPatternsCondition得到请求url数组，遍历处理
                    PatternsRequestCondition patternsCondition = info.getPatternsCondition();
                    Set<String> patterns = patternsCondition.getPatterns();
                    patterns.forEach(pattern -> {
                        try {
                            http.antMatcher(pattern).anonymous();
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    });
                }
            });
        });


    }

    @Resource
    private RequestMappingHandlerMapping requestMappingHandlerMapping;

    /**
     * @ description: 使用这种方式放行的接口，不走 Spring Security 过滤器链，
     * 无法通过 SecurityContextHolder 获取到登录用户信息，
     * 因为它一开始没经过 SecurityContextPersistenceFilter 过滤器链。
     */
    @Override
    public void configure(WebSecurity web) throws Exception {
        // 放行webSocket
        web.ignoring().antMatchers("/net/**");
    }
}
