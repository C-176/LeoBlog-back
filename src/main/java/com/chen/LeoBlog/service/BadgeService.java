package com.chen.LeoBlog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chen.LeoBlog.base.ResultInfo;
import com.chen.LeoBlog.po.Badge;

import java.util.Map;

/**
 * @author rtg19
 * @description 针对表【lb_badge】的数据库操作Service
 * @createDate 2023-06-11 18:31:50
 */
public interface BadgeService extends IService<Badge> {

    ResultInfo getBadgeById(String badgeId);

    ResultInfo getAllBadges();

    ResultInfo addBadge(Map<String, Object> badge);

    ResultInfo updateBadge(Map<String, Object> map);

    ResultInfo deleteBadgeById(String badgeId);

    ResultInfo buyBadge(Long badgeId);

    ResultInfo buyLimitedBadge(Long badgeId);

    ResultInfo getUserBadges(Long userId);
}
