package com.chen.LeoBlog.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface RequiredPermission {
    String code() default "";
}
