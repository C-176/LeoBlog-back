package com.chen.LeoBlog.controller;

import com.chen.LeoBlog.annotation.Anonymous;
import com.chen.LeoBlog.base.ResultInfo;
import com.chen.LeoBlog.service.ChatRecordService;
import com.chen.LeoBlog.vo.request.CursorPageBaseReqWithUserId;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@Slf4j
@CrossOrigin
@RequestMapping("/chat/record")
public class ChatRecordController {

    @Resource
    private ChatRecordService chatRecordService;

    @DeleteMapping("/{recordId}")
    public ResultInfo deleteRecord(@PathVariable Long recordId) {
        return chatRecordService.deleteRecord(recordId);
    }

    @GetMapping("/list/{userId}/{chatToId}/{page}/{size}")
    public ResultInfo getRecordList(@PathVariable Long userId, @PathVariable Long chatToId, @PathVariable Integer page, @PathVariable Integer size) {
        return chatRecordService.getRecordList(userId, chatToId, page, size);
    }


    @PostMapping("/cursor/list")
    public ResultInfo<?> getCursorPage(@RequestBody CursorPageBaseReqWithUserId cursorPageBaseReq) {
        return chatRecordService.getCursorPage(cursorPageBaseReq);
    }

    @Anonymous
    @GetMapping("/add/example")
    public ResultInfo<?> getCursorPage() {
        return chatRecordService.addExample();
    }
}
