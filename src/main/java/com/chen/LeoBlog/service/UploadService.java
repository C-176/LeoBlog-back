package com.chen.LeoBlog.service;


import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.UUID;
import com.chen.LeoBlog.utils.AssertUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.*;


@Service
@Slf4j
public class UploadService {
    //端口号
    @Value("${server.port}")
    private int port;
    @Value("${static-path}")
    private String staticPath;


    /**
     * 上传文件至指定目录,return上传后的文件名
     */
    public String uploadFile(MultipartFile file, String path) {
        //设置上传文件大小上限为2M
        AssertUtil.isTrue(file.getSize() > 2097152, "上传文件大小不能超过2M");
        AssertUtil.isTrue(file.isEmpty(), "上传文件不能为空");
        String filename = file.getOriginalFilename();
        // 获取文件扩展名
        String suffix = FileUtil.getSuffix(filename);

        // UUID，防止命名冲突导致覆盖
        String uploadFilename = UUID.randomUUID(true) + "." + suffix;
        //TODO:实现图片与文章或者草稿绑定，使删除文章时图片一并删掉

        // 检查上传路径是否存在，不存在则创建
        if (!new File(path).exists()) {
            FileUtil.mkdir(path);
        }

        File targetFile = new File(path, uploadFilename);

        try {
            file.transferTo(targetFile);
        } catch (IOException e) {
            AssertUtil.isTrue(true, "文章中图片上传失败");
            return null;
        }
        return "/source/upload/images/" + uploadFilename;
    }

    /**
     * 文章中的图片上传
     */
    public Map uploadEditorImage(MultipartFile file) {
        String imageUrl;
        try {
            imageUrl = uploadFile(file, staticPath);
            log.debug("文件所在地：{}",imageUrl);
        } catch (Exception e) {
            log.error("文件上传失败", e);
            Map<String, Object> map = new HashMap<>();
            map.put("errno", 1);
            map.put("message", e.getMessage());
            return map;
        }
        Map<String, Object> map = new HashMap<>();
        map.put("errno", 0);
        Map<String, Object> data = new HashMap<>();
        data.put("url", imageUrl);
        data.put("alt", "图片描述");
        data.put("href", "图片链接");
        map.put("data", data);
        return map;

    }

}
