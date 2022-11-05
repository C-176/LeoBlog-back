package com.chen.LeoBlog.test;


import com.chen.LeoBlog.Starter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.BoundListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Starter.class},webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)

public class RedisTest {
    @Autowired
    StringRedisTemplate redisTemplate;
    @Autowired
    DataSource dataSource;

    @Test
    public void testRedis() throws SQLException {
        String sql = "select name,pwd from user where id = ?";
        Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1, 1);
        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
            System.out.println(resultSet.getString(0));
            System.out.println(resultSet.getString("pwd"));

        }

    }
}
