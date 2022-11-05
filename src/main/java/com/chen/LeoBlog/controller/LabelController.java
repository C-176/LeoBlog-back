package com.chen.LeoBlog.controller;

import com.chen.LeoBlog.base.ResultInfo;
import com.chen.LeoBlog.service.LabelService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@Slf4j
@RequestMapping("/label")
public class LabelController {
    @Resource
    private LabelService labelService;

    @GetMapping("/list/article/{articleId}")
    public ResultInfo getLabelList(@PathVariable("articleId") Long articleId){
        return labelService.getLabelList(articleId);
    }
    @GetMapping("/list")
    public ResultInfo getLabelList(){
        return labelService.getLabelList();
    }

}
