package com.chen.LeoBlog.annotation;

import java.lang.annotation.*;

@Target({ElementType.METHOD}) // 该注解作用在方法上
@Retention(RetentionPolicy.RUNTIME) // 运行时生效
@Documented
public @interface Authorized {
    String code() default "";
}
