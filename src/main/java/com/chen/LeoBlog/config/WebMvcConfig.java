package com.chen.LeoBlog.config;

import com.chen.LeoBlog.interceptors.AuthorizedInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

@Configuration
@Slf4j
public class WebMvcConfig implements WebMvcConfigurer {
    @Value("${static-path}")
    private String path;

    @Resource
    private AuthorizedInterceptor authorizedInterceptor;

    // 静态资源映射
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        log.info("静态资源映射");
        //关于图片上传后需要重启服务器才能刷新图片
        //这是一种保护机制，为了防止绝对路径被看出来，目录结构暴露
        //解决方法:将虚拟路径/source/upload/images/
        //        向绝对路径 (D:\\Javacode\\LeoBlog\\src\\main\\resources\\static\\source\\upload\\images\\)映射
        registry.addResourceHandler("/**").addResourceLocations("file:" + path);

    }


    //拦截器
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        /**
         * excludePathPatterns 方法接受一个或多个 Ant 样式路径表达式，用于匹配需要放行的请求路径。Ant 样式路径表达式是一种通配符，用于匹配多个路径。其中，* 表示匹配任意数量的字符，** 表示匹配任意数量的路径，? 表示匹配任意一个字符，{} 表示匹配一组选择项。
         * 以下是一些示例，展示了如何使用 Ant 样式路径表达式来配置 excludePathPatterns：
         * excludePathPatterns("/api/user/**") 表示放行以 /api/user/ 开头的任意请求路径，包括 "/api/user/123"、"/api/user/login"、"/api/user/info/123" 等。
         * excludePathPatterns("/api/user/{id}") 表示放行形如 /api/user/{id} 的请求路径，其中 {id} 表示一个变量，可以匹配任意字符串。
         * excludePathPatterns("/api/user/{id:[0-9]+}") 表示放行形如 /api/user/{id} 的请求路径，其中 {id:[0-9]+} 表示一个变量，只能匹配数字。
         */
        registry.addInterceptor(authorizedInterceptor).addPathPatterns("/**")
                .excludePathPatterns("/source/**", "/v2/**", "/favicon.ico"
                );
    }

    //    CORS跨域配置
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")   // 允许跨域访问的路径
                .allowedOriginPatterns("*")  // 允许跨域访问的源
                .allowedMethods("GET", "POST", "DELETE", "PUT")  // 允许请求方法
                .maxAge(666000)  // 预检间隔时间
                .allowedHeaders("*")  // 允许头部设置
                .allowCredentials(true); // 是否允许发送cookie
        log.info("CORS配置成功");
    }
}

