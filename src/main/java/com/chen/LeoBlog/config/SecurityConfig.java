package com.chen.LeoBlog.config;

import com.chen.LeoBlog.filter.LoginFilter;
import com.chen.LeoBlog.handler.SecurityEntryPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.EnableGlobalAuthentication;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.annotation.Resource;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Resource
    private LoginFilter loginFilter;

    @Resource
    private SecurityEntryPoint securityEntryPoint;
    // 暴露

//    // 修改默认的密码加密方式
//     @Bean
//     public PasswordEncoder passwordEncoder() {
//         return new BCryptPasswordEncoder();
//     }

    @Bean("authenticationManager")
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }


    @Override

    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .authorizeRequests()
                .antMatchers("/source/**", "/upload/**", "/article/*", "/comment/*",
                        "/article/list/*/*", "/comment/list/*/*", "/badge/*",
                        "/user/login", "/user/register", "/user/confirm/**",
                        "/user/getCaptcha", "/user/changePwd","/user/**",
                        "/user/[d]+").permitAll()
                .anyRequest().authenticated();
//                .and()
//                .formLogin()
//                .and()
//                .logout().logoutUrl("/logout").logoutSuccessUrl("/login").permitAll();
        http.addFilterBefore(loginFilter, UsernamePasswordAuthenticationFilter.class);
        // 添加验证失败和鉴权失败异常处理器，统一响应格式
        http.exceptionHandling().authenticationEntryPoint(securityEntryPoint)
                .accessDeniedHandler(securityEntryPoint);
        // 允许跨域
        http.cors();
    }

    @Override
    public void configure(WebSecurity web) {
        web.ignoring().antMatchers("/net/**");
    }
}
