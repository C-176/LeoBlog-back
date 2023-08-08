package com.chen.LeoBlog.controller;

import com.chen.LeoBlog.annotation.Anonymous;
import com.chen.LeoBlog.base.ResultInfo;
import com.chen.LeoBlog.service.BadgeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/badge")
public class BadgeController {
    @Resource
    private BadgeService badgeService;

    @Anonymous
    @GetMapping("/{badgeId}")
    public ResultInfo getBadgeById(@PathVariable String badgeId) {
        return badgeService.getBadgeById(badgeId);
    }

    @Anonymous
    @GetMapping("/all")
    public ResultInfo getAllBadges() {
        return badgeService.getAllBadges();
    }

    @PostMapping("/add")
    public ResultInfo addBadge(@RequestBody Map<String, Object> badge) {
        return badgeService.addBadge(badge);
    }

    @PutMapping("/update")
    public ResultInfo updateBadge(@RequestBody Map<String, Object> map) {
        return badgeService.updateBadge(map);
    }

    @DeleteMapping("/{badgeId}")
    public ResultInfo deleteBadgeById(@PathVariable String badgeId) {
        return badgeService.deleteBadgeById(badgeId);
    }

    // 购买徽章
    @PostMapping("/buy/{badgeId}")
    public ResultInfo buyBadge(@PathVariable Long badgeId) {
        return badgeService.buyBadge(badgeId);
    }

    // 购买限量徽章
    @PostMapping("/buy/limited/{badgeId}")
    public ResultInfo buyLimitedBadge(@PathVariable Long badgeId) {
        return badgeService.buyLimitedBadge(badgeId);
    }

    @GetMapping("/user/{userId}")
    public ResultInfo getUserBadges(@PathVariable Long userId) {
        return badgeService.getUserBadges(userId);
    }


}
