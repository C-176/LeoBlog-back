package com.chen.LeoBlog.service;

import com.chen.LeoBlog.base.ResultInfo;
import com.chen.LeoBlog.po.Label;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author 1
* @description 针对表【lb_label】的数据库操作Service
* @createDate 2022-10-14 17:36:20
*/
public interface LabelService extends IService<Label> {

    ResultInfo getLabelList(Long articleId);
    ResultInfo getLabelList();

}
