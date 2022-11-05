package com.chen.LeoBlog.test;

import com.chen.LeoBlog.Starter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Starter.class},webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)

public class LogBackTest {
    private final static Logger logger = LoggerFactory.getLogger(LogBackTest.class);

    @Test
    public void testLogback(){
        //打印日志信息
        logger.error("error");
        logger.warn("warn");
        logger.info("info");
        logger.debug("debug");
        logger.trace("trace");
    }
}
