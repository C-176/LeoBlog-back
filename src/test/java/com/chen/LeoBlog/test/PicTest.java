package com.chen.LeoBlog.test;

import com.chen.LeoBlog.Starter;
import com.chen.LeoBlog.po.User;
import com.chen.LeoBlog.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Starter.class},webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PicTest {
    @Autowired
    private UserService userService;

    @Test
    public void test(){
        User userById = userService.getById(21);
        System.out.println(userById);
    }
}
