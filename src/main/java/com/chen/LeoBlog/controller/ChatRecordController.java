package com.chen.LeoBlog.controller;

import com.chen.LeoBlog.base.ResultInfo;
import com.chen.LeoBlog.service.ChatRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@CrossOrigin
@RequestMapping("/chat/record")
public class ChatRecordController {

    @Autowired
    private ChatRecordService chatRecordService;

    @DeleteMapping("/{recordId}")
    public ResultInfo deleteRecord(@PathVariable Long recordId) {
        return chatRecordService.deleteRecord(recordId);
    }

    @GetMapping("/list/{userId}/{chatToId}/{page}/{size}")
    public ResultInfo getRecordList(@PathVariable Long userId, @PathVariable Long chatToId, @PathVariable Integer page, @PathVariable Integer size) {
        return chatRecordService.getRecordList(userId, chatToId, page, size);
    }

}
