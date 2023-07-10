package com.chen.LeoBlog.controller;


import com.chen.LeoBlog.base.ResultInfo;
import com.chen.LeoBlog.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

@Slf4j
@RestController
@CrossOrigin
@RequestMapping("/message")
public class MessageController {
    @Resource
    private MessageService messageService;

    @GetMapping("/user/{userId}/{page}/{size}")
    public ResultInfo getMsgByUserId(@PathVariable("userId") Long userId, @PathVariable(value="page") Integer page, @PathVariable("size") Integer size) {
        return messageService.getMsgByUserId(userId, page, size);
    }

    @GetMapping("/{offset}/{lastScore}")
    public ResultInfo getMsgFromBox(@PathVariable("offset") Integer offset, @PathVariable("lastScore") Long lastScore) {
        return messageService.getMsgFromBox(offset, lastScore);
    }


//    @PostMapping("/add")
//    public ResultInfo addMessage(@RequestBody Map<String, Object> map) {
//        return messageService.addMessage(map);
//    }

    @DeleteMapping("/{messageId}")
    public ResultInfo deleteMessage(@PathVariable("messageId") Long messageId) {
        return messageService.deleteMessage(messageId);
    }

    @PutMapping("/read/{messageId}")
    public ResultInfo readMessage(@PathVariable("messageId") Long messageId) {
        return messageService.readMessage(messageId);
    }
}
