package com.chen.LeoBlog.config;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import com.yupi.yucongming.dev.client.YuCongMingClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BaseConfig {
    @Value("${yuapi.client.access-key}")
    String accessKey;

    @Value("${yuapi.client.secret-key}")
    String secretKey;

    @Bean
    public YuCongMingClient yuCongMingClient() {
        return new YuCongMingClient(accessKey, secretKey);
    }


    @Bean
    public LineCaptcha lineCaptcha() {
        return CaptchaUtil.createLineCaptcha(116, 36, 4, 20);
    }


}
