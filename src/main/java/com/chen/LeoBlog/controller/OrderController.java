package com.chen.LeoBlog.controller;

import com.chen.LeoBlog.base.ResultInfo;
import com.chen.LeoBlog.po.Order;
import com.chen.LeoBlog.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@Slf4j
@RequestMapping("/order")
public class OrderController {

    @Resource
    private OrderService orderService;

    @GetMapping("/all")
    public ResultInfo<String> getAllOrders() {
        return orderService.getAllOrders();
    }

    @PostMapping("/add")
    public ResultInfo<String> addOrder(@RequestBody Order order) {
        return orderService.addOrder(order);
    }
    // 更新订单状态
    @PutMapping("/update")
    public ResultInfo<String> updateOrder(@RequestBody Order order) {
        return orderService.updateOrder(order);
    }
    // 删除订单
    @DeleteMapping("/{orderId}")
    public ResultInfo<String> deleteOrder(@PathVariable Long orderId) {
        return orderService.deleteOrder(orderId);
    }

}
