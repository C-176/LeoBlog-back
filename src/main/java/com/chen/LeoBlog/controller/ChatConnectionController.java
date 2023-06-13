package com.chen.LeoBlog.controller;

import com.chen.LeoBlog.base.ResultInfo;
import com.chen.LeoBlog.service.ChatConnectionService;
import com.chen.LeoBlog.service.ChatRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/chat")
@Slf4j

public class ChatConnectionController {
    @Autowired
    private ChatConnectionService chatConnectionService;
    @Autowired
    private ChatRecordService chatRecordService;

    @GetMapping("/connect/{userId}/{chatToId}")
    public ResultInfo connect(@PathVariable("userId") Long userId, @PathVariable("chatToId") Long chatToId) {
        return chatConnectionService.connect(userId, chatToId);
    }

    @GetMapping("/list/{userId}")
    public ResultInfo getChatConnectionList(@PathVariable Long userId) {
        return chatConnectionService.getChatConnectionList(userId);
    }


}
