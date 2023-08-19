package com.chen.LeoBlog.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD) // 该注解作用在方法上
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Anonymous {
}
