package com.chen.LeoBlog.controller;

import com.chen.LeoBlog.annotation.Anonymous;
import com.chen.LeoBlog.base.ResultInfo;
import com.chen.LeoBlog.po.Label;
import com.chen.LeoBlog.service.LabelService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

@RestController
@Slf4j
@CrossOrigin
@RequestMapping("/label")
public class LabelController {
    @Resource
    private LabelService labelService;

    @Anonymous
    @GetMapping("/list/article/{articleId}")
    public ResultInfo getLabelList(@PathVariable("articleId") Long articleId){
        return labelService.getLabelList(articleId);
    }
    @Anonymous
    @GetMapping("/list")
    public ResultInfo getLabelList(){
        return labelService.getLabelList();
    }

    @PostMapping("/add")
    public ResultInfo addLabel(@RequestBody Map<String, Object> map){
        return labelService.addLabel(map);
    }
    @PutMapping("/update")
    public ResultInfo updateLabel(@RequestBody Map<String, Object> map){
        return labelService.updateLabel(map);
    }
    @DeleteMapping("/{labelId}")
    public ResultInfo deleteLabel(@PathVariable("labelId") Long labelId){
        return labelService.deleteLabel(labelId);
    }

}
