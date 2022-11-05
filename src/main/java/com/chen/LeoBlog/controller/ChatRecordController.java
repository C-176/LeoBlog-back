package com.chen.LeoBlog.controller;

import com.chen.LeoBlog.base.ResultInfo;
import com.chen.LeoBlog.service.ChatRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/chat/record")
public class ChatRecordController {

    @Autowired
    private ChatRecordService chatRecordService;

    @DeleteMapping("/{recordId}")
    public ResultInfo deleteRecord(@PathVariable Long recordId) {
        return chatRecordService.deleteRecord(recordId);
    }
}
