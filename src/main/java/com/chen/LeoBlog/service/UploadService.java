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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
@Slf4j
public class UploadService {
    //端口号
    @Value("${static-path}")
    private String staticPath;
    @Value("${ip}")
    private String ip;


    /**
     * 上传文件至指定目录,return上传后的文件名
     */
    public String uploadFile(MultipartFile file, String path) {
        //判断文件类型是视频还是图片
        String fileType = file.getContentType();
        String[] videoType = {"video/mp4", "video/avi", "video/mpeg4", "video/mpeg"};
        String[] imgType = {"image/jpeg", "image/png", "image/gif", "image/bmp", "image/webp"};
        List<String> videoList = Arrays.asList(videoType);
        List<String> imgList = Arrays.asList(imgType);
        //判断文件类型是否正确
        AssertUtil.isTrue(!(videoList.contains(fileType) || imgList.contains(fileType)), "上传文件类型不正确，请上传视频（.mp4/.avi/.mpeg4/.mpeg）或图片");
        boolean isImage = imgList.contains(fileType);
        if (isImage) {
            path = path + "source/upload/images/";
        } else {
//            将文件转化为mp4格式
            path = path + "source/upload/videos/";
        }

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
            log.error("上传文件失败", e);
            AssertUtil.isTrue(true, "文章中图片上传失败");
            return null;
        }
        return !isImage ? "/source/upload/videos/" + uploadFilename : "/source/upload/images/" + uploadFilename;
    }

    /**
     * 文章中的图片上传
     */
    public Map uploadEditorFile(MultipartFile file) {
//        String[] videoType = {"video/mp4", "video/avi", "video/mpeg4", "video/mpeg"};
//        List<String> videoList = Arrays.asList(videoType);
//        boolean isVideo = videoList.contains(file.getContentType());

        String imageUrl;
        try {
            imageUrl = uploadFile(file, staticPath);
            log.debug("文件所在地：{}", imageUrl);
        } catch (
                Exception e) {
            log.error("文件上传失败", e);
            Map<String, Object> map = new HashMap<>();
            map.put("errno", 1);
            map.put("message", e.getMessage());
            return map;
        }
        //获取请求地址


        Map<String, Object> map = new HashMap<>();
        map.put("errno", 0);
        Map<String, Object> data = new HashMap<>();
        data.put("url", "http://" + ip + imageUrl);
        map.put("data", data);
        return map;

    }

}
