package com.chen.LeoBlog;

import com.chen.LeoBlog.annotation.Anonymous;
import com.chen.LeoBlog.factory.SparkDeskAi;
import com.chen.LeoBlog.factory.SparkDeskAiFactory;
import okhttp3.WebSocket;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;


@SpringBootApplication
@EnableScheduling
@Controller
public class Starter extends SpringBootServletInitializer {

    public static void main(String[] args) {

        SpringApplication springApplication = new SpringApplication(Starter.class);

        springApplication.setBannerMode(Banner.Mode.OFF); // banner图标关闭

        springApplication.run();
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(Starter.class);
    }

    @Anonymous
    @RequestMapping("/test/{xx}")
    public void test(@PathVariable String xx) throws InterruptedException {
        SparkDeskAi instance = SparkDeskAiFactory.getInstance(1L);
        WebSocket webSocket = instance.getWebSocket();
        if (webSocket == null) {
            Thread.sleep(1000);
        }
        instance.send("介绍一下周杰伦");

    }

}
