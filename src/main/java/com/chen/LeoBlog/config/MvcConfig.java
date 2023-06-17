package com.chen.LeoBlog.config;


import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import io.lettuce.core.RedisClient;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.sql.DataSource;
import java.beans.PropertyVetoException;
import java.io.ObjectInputFilter;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
public class MvcConfig extends WebMvcConfigurerAdapter {
//    @Value("${spring.datasource.type}")
//    private String type;
    @Autowired
    private Environment environment;
//    @Value("${spring.datasource.driver-class-name}")
//    private String className;
//    @Value("${spring.datasource.url}")
//    private String url;
//    @Value("${spring.datasource.username}")
//    private String username;
//    @Value("${spring.datasource.password}")
//    private String password;

    @Bean
    public DataSource getSource() throws PropertyVetoException {
        ComboPooledDataSource dataSource = new ComboPooledDataSource();
        dataSource.setDriverClass(environment.getProperty("spring.datasource.driver-class-name"));
        dataSource.setJdbcUrl(environment.getProperty("spring.datasource.url"));
        dataSource.setUser(environment.getProperty("spring.datasource.username"));
        dataSource.setPassword(environment.getProperty("spring.datasource.password"));
        //链接池的最小连接数
        dataSource.setMinPoolSize(3);
        // 超时重试次数
        dataSource.setAcquireRetryAttempts(30);
        //测试链接
        dataSource.setTestConnectionOnCheckin(true);
        //检查所有连接池中的空闲连接 120S
        dataSource.setIdleConnectionTestPeriod(120);
        return dataSource;
    }

    @Bean
    public Executor asyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        //随便写的一些配置
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(20);
        executor.setQueueCapacity(200);
        executor.setKeepAliveSeconds(60);
        executor.setThreadNamePrefix("asyncExecutor-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        return executor;
    }
    @Bean
    public LineCaptcha lineCaptcha() {
        return CaptchaUtil.createLineCaptcha(116, 36, 4, 20);
    }

    @Bean
    public RedissonClient redissonClient(){
        // 1. Create config object
        Config config = new Config();
        config.useSingleServer()
                // use "redis://" for SSL connection
                .setAddress("redis://"+environment.getProperty("spring.redis.host")+":"+environment.getProperty("spring.redis.port"))
                .setPassword(environment.getProperty("spring.redis.password"));
        return Redisson.create(config);
    }



}
