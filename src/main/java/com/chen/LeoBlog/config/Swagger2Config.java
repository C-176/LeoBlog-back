package com.chen.LeoBlog.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class Swagger2Config {

    @Bean
    public Docket webApiConfig(){
        return new Docket(DocumentationType.SWAGGER_2).groupName("webApi")
                .apiInfo(webApiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.chen.LeoBlog"))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo webApiInfo(){
        return new ApiInfoBuilder().title("网站的API文档")
                .description("本文档描述了api接口定义")
                .version("1.0")
                .contact(new Contact("xiaolei","https://juejin.cn/user/96412756360526","***@qq.com"))
                .build();
    }
}