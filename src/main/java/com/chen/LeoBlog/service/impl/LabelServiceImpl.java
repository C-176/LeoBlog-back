package com.chen.LeoBlog.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.map.MapUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chen.LeoBlog.base.ResultInfo;
import com.chen.LeoBlog.po.Label;
import com.chen.LeoBlog.service.LabelService;
import com.chen.LeoBlog.mapper.LabelMapper;
import com.chen.LeoBlog.service.SetArticleLabelService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author 1
 * @description 针对表【lb_label】的数据库操作Service实现
 * @createDate 2022-10-14 17:36:20
 */
@Service
@Slf4j
public class LabelServiceImpl extends ServiceImpl<LabelMapper, Label>
        implements LabelService {
    @Resource
    private SetArticleLabelService setArticleLabelService;

    @Override
    public ResultInfo getLabelList(Long articleId) {
        List<Long> ids = setArticleLabelService.getLabelList(articleId);
        List<Label> list = new ArrayList<>();
        if (ids.size() != 0) {
            list = query().in("label_id", ids).last("order by field(label_id," + String.join(",", ids.stream().map(String::valueOf).toList()) + ")").list();
        }
        return ResultInfo.success(list);

    }

    @Override
    public ResultInfo getLabelList() {
        List<Label> list = query().list();
        return ResultInfo.success(list);
    }

    @Override
    public ResultInfo addLabel(Map<String, Object> map) {
        Label label = BeanUtil.toBean(map, Label.class);
        if (save(label)) {
            return ResultInfo.success(query().eq("label_name", label.getLabelName()).one());
        }
        return ResultInfo.fail("添加失败");
    }

    @Override
    public ResultInfo updateLabel(Map<String, Object> map) {
        Label label = BeanUtil.toBean(map, Label.class);
        if (updateById(label)) {
            return ResultInfo.success(query().eq("label_name", label.getLabelName()).one());
        }
        return ResultInfo.fail("修改失败");
    }

    @Override
    public ResultInfo deleteLabel(Long labelId) {
        if (removeById(labelId)) {
            return ResultInfo.success("删除成功");
        }
        return ResultInfo.fail("删除失败");
    }
}




