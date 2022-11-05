package com.chen.LeoBlog.test;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chen.LeoBlog.Starter;
import com.chen.LeoBlog.po.User;
import com.chen.LeoBlog.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Starter.class},webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)

public class MpTest {
    @Resource
    private UserService userService;


    @Test
    public void mpTest(){
        int currentPage = 2;
        int pageSize = 5;
        Page<User> iPage = new Page<>(currentPage,pageSize);
        userService.page(iPage);
        userService.list();

//        iPage.getRecords().forEach(System.out::println);
        System.out.println(iPage.getPages());
        System.out.println(iPage.getTotal());
        System.out.println(iPage.getSize());
        System.out.println(iPage.getCurrent());
//        userMapper.selectPage(iPage,null);
////        userMapper.selectList(new LambdaQueryWrapper<>(new User()).gt(User::getUserId,1));
//        iPage.getRecords().forEach(System.out::println);
//        System.out.println(iPage.getPages());
//        System.out.println(iPage.getCurrent());
//        System.out.println(iPage.getSize());


    }

}