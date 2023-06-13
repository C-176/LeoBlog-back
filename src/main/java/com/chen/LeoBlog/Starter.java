package com.chen.LeoBlog;

import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.bind.annotation.CrossOrigin;


@SpringBootApplication

public class Starter extends SpringBootServletInitializer {

    public static void main(String[] args) {

        SpringApplication springApplication = new SpringApplication(Starter.class);

        springApplication.setBannerMode(Banner.Mode.OFF); // banner图标关闭

        ConfigurableApplicationContext run = springApplication.run();
//        for (String beanDefinitionName : run.getBeanDefinitionNames()) {
//            System.out.println(beanDefinitionName);
//        }
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(Starter.class);
    }

}
