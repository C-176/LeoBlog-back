package com.chen.LeoBlog.service;

import com.chen.LeoBlog.base.ResultInfo;
import com.chen.LeoBlog.po.Order;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author rtg19
* @description 针对表【lb_order】的数据库操作Service
* @createDate 2023-06-11 18:29:13
*/
public interface OrderService extends IService<Order> {


    ResultInfo<String> getAllOrders();

    ResultInfo<String> addOrder(Order order);

    ResultInfo<String> updateOrder(Order order);

    ResultInfo<String> deleteOrder(Long orderId);
}
