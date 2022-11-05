package com.chen.LeoBlog.service;


import com.chen.LeoBlog.po.SetArticleLabel;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author 1
* @description 针对表【lb_set_article_label】的数据库操作Service
* @createDate 2022-10-21 22:51:46
*/
public interface SetArticleLabelService extends IService<SetArticleLabel> {
    List<Long> getLabelList(Long articleId);
    boolean setLabelList(Long articleId, List<Long> labelIds);
}
