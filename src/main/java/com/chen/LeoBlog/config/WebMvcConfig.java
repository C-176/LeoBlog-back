package com.chen.LeoBlog.config;

import com.chen.LeoBlog.interceptors.NoLoginInterceptor;
import com.chen.LeoBlog.interceptors.RefreshTTLInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@Slf4j
public class WebMvcConfig implements WebMvcConfigurer {
    @Value("${static-path}")
    private String path;

    @Autowired
    private StringRedisTemplate redisTemplate;

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
        registry.addInterceptor(new RefreshTTLInterceptor(redisTemplate)).addPathPatterns("/**");
        registry.addInterceptor(new NoLoginInterceptor(redisTemplate)).addPathPatterns("/xx/**")
                .excludePathPatterns("/user/**","/source/**");
    }

//    CORS
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")   // 允许跨域访问的路径
                .allowedOriginPatterns("*")  // 允许跨域访问的源
                .allowedMethods("*")  // 允许请求方法
                .maxAge(666000)  // 预检间隔时间
                .allowedHeaders("*")  // 允许头部设置
                .allowCredentials(true); // 是否允许发送cookie
        log.info("CORS配置成功");
    }
}

