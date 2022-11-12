package com.chen.LeoBlog.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chen.LeoBlog.mapper.SetArticleLabelMapper;
import com.chen.LeoBlog.po.SetArticleLabel;
import com.chen.LeoBlog.service.SetArticleLabelService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author 1
* @description 针对表【lb_set_article_label】的数据库操作Service实现
* @createDate 2022-10-21 22:51:46
*/
@Service
@Slf4j
public class SetArticleLabelServiceImpl extends ServiceImpl<SetArticleLabelMapper, SetArticleLabel>
    implements SetArticleLabelService{


    @Override
    public List<Long> getLabelList(Long articleId) {
        List<SetArticleLabel> list = query().eq("article_id", articleId).list();
        List<Long> ids = list.stream().map(SetArticleLabel::getLabelId).toList();
        return ids;
    }

    @Override
    public boolean setLabelList(Long articleId, List<Long> labelIds) {
        if (labelIds == null || labelIds.size() == 0) {
            log.info("标签为空");
            return true;
        }
        for (Long labelId : labelIds) {
            SetArticleLabel one = query().eq("article_id", articleId).eq("label_id", labelId).one();
            if (one == null) {
                SetArticleLabel setArticleLabel = new SetArticleLabel();
                setArticleLabel.setArticleId(articleId);
                setArticleLabel.setLabelId(labelId);
                save(setArticleLabel);
            }
        }
        update().eq("article_id", articleId).notIn("label_id", labelIds).remove();
        return true;
    }
}




