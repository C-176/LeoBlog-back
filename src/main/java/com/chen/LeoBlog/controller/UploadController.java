package com.chen.LeoBlog.controller;


import com.chen.LeoBlog.annotation.Anonymous;
import com.chen.LeoBlog.base.FileUploadResp;
import com.chen.LeoBlog.service.UploadService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

@RestController
@Slf4j
@CrossOrigin
@RequestMapping("/upload")

public class UploadController {
    @Resource
    private UploadService uploadService;

    /**
     * 图片上传，返回图片地址信息。
     */
    @Anonymous
    @PostMapping("/file")
    public FileUploadResp uploadImage(@RequestParam("file") MultipartFile file) {
        return uploadService.uploadEditorFile(file);
    }

}
