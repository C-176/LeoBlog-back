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
import java.io.Serializable;
import java.util.*;

import static com.chen.LeoBlog.constant.BaseConstant.UPLOAD_IMG_PATH;
import static com.chen.LeoBlog.constant.BaseConstant.UPLOAD_VIDEO_PATH;


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
        Set<String> videoSet = Set.of(videoType);
        Set<String> imgSet = Set.of(imgType);
        //判断文件类型是否正确
        AssertUtil.isTrue(!videoSet.contains(fileType) && !imgSet.contains(fileType), "上传文件类型不正确，请上传视频（.mp4/.avi/.mpeg4/.mpeg）或图片");

        path = path + (imgSet.contains(fileType) ? UPLOAD_IMG_PATH :UPLOAD_VIDEO_PATH);

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
        return (imgSet.contains(fileType) ? UPLOAD_IMG_PATH :UPLOAD_VIDEO_PATH) + uploadFilename;
    }

    /**
     * 文章中的图片上传
     */
    public Map<String, Object> uploadEditorFile(MultipartFile file) {
//        String[] videoType = {"video/mp4", "video/avi", "video/mpeg4", "video/mpeg"};
//        List<String> videoList = Arrays.asList(videoType);
//        boolean isVideo = videoList.contains(file.getContentType());

        String imageUrl;
        try {
            imageUrl = uploadFile(file, staticPath);
            log.debug("文件所在地：{}", imageUrl);
        } catch (Exception e) {
            log.error("文件上传失败", e);
            return Map.of("errno",1,"message",e.getMessage());
        }
        //获取请求地址
        return Map.of("errno", 0, "url", "http://" + ip + imageUrl);

    }

}
