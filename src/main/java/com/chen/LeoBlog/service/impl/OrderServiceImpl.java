package com.chen.LeoBlog.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chen.LeoBlog.base.ResultInfo;
import com.chen.LeoBlog.po.Order;
import com.chen.LeoBlog.service.OrderService;
import com.chen.LeoBlog.mapper.OrderMapper;
import org.springframework.stereotype.Service;
import springfox.documentation.spring.web.json.Json;

import java.util.List;

/**
 * @author rtg19
 * @description 针对表【lb_order】的数据库操作Service实现
 * @createDate 2023-06-11 18:29:13
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order>
        implements OrderService {

    @Override
    public ResultInfo<String> getAllOrders() {
        List<Order> list = query().list();
        return ResultInfo.success(JSONUtil.toJsonStr(list));
    }

    @Override
    public ResultInfo<String> addOrder(Order order) {
        boolean isSuccess = save(order);
        if (!isSuccess) {
            return ResultInfo.fail("添加失败");
        }

        return ResultInfo.success();
    }

    @Override
    public ResultInfo<String> updateOrder(Order order) {
        return null;
    }

    @Override
    public ResultInfo<String> deleteOrder(Long orderId) {
        boolean isSuccess = removeById(orderId);
        if (!isSuccess) {
            return ResultInfo.fail("删除失败");
        }
        return ResultInfo.success();
    }
}




