package com.chen.LeoBlog.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chen.LeoBlog.base.ResultInfo;
import com.chen.LeoBlog.po.Label;
import com.chen.LeoBlog.service.LabelService;
import com.chen.LeoBlog.mapper.LabelMapper;
import com.chen.LeoBlog.service.SetArticleLabelService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
* @author 1
* @description 针对表【lb_label】的数据库操作Service实现
* @createDate 2022-10-14 17:36:20
*/
@Service
public class LabelServiceImpl extends ServiceImpl<LabelMapper, Label>
    implements LabelService{
    @Resource
    private SetArticleLabelService setArticleLabelService;
    @Override
    public ResultInfo getLabelList(Long articleId) {
        List<Long> ids = setArticleLabelService.getLabelList(articleId);
        List<Label> list = new ArrayList<>();
        if(ids.size() != 0){
            list = query().in("label_id", ids).last("order by field(label_id," + String.join(",", ids.stream().map(String::valueOf).toList()) + ")").list();
        }
        return ResultInfo.success(list);

    }

    @Override
    public ResultInfo getLabelList() {
        List<Label> list = query().list();
        return ResultInfo.success(list);
    }
}




