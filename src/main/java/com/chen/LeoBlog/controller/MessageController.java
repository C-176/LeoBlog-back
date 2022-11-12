package com.chen.LeoBlog.controller;


import com.chen.LeoBlog.base.ResultInfo;
import com.chen.LeoBlog.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/message")
public class MessageController {
    @Autowired
    private MessageService messageService;

    @GetMapping("/user/{userId}")
    public ResultInfo getMsgByUserId(@PathVariable("userId") Long userId) {
        return messageService.getMsgByUserId(userId);
    }

    @PostMapping("/add")
    public ResultInfo addMessage(@RequestBody Map<String, Object> map) {
        return messageService.addMessage(map);
    }

    @DeleteMapping("/{id}")
    public ResultInfo deleteMessage(@PathVariable("id") Long id) {
        return messageService.deleteMessage(id);
    }
}
