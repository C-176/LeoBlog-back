package com.chen.LeoBlog.controller;


import com.chen.LeoBlog.annotation.Anonymous;
import com.chen.LeoBlog.service.UploadService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.Map;

@RestController
@Slf4j
@CrossOrigin
@RequestMapping("/upload")

public class UploadController {
    @Autowired
    private UploadService uploadService;

    /**
     * 图片上传，返回图片地址信息。
     */
    @Anonymous
    @PostMapping("/file")
    public Map<String, Object> uploadImage(@RequestParam("file") MultipartFile file) {
        return uploadService.uploadEditorFile(file);
    }

}
