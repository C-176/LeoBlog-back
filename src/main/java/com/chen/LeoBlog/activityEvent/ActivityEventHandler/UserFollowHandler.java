package com.chen.LeoBlog.activityEvent.ActivityEventHandler;

import cn.hutool.json.JSONUtil;
import com.chen.LeoBlog.activityEvent.ActivityEvent;
import com.chen.LeoBlog.activityEvent.ActivityEventEnum;
import com.chen.LeoBlog.constant.RedisConstant;
import com.chen.LeoBlog.dto.UserDTO;
import com.chen.LeoBlog.po.Message;
import com.chen.LeoBlog.service.UserService;
import com.chen.LeoBlog.utils.BaseUtil;
import com.chen.LeoBlog.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Set;


@Component
@Slf4j
public class UserFollowHandler extends AbstractActivityEventHandler {

    @Resource
    private UserService userService;

    @Override
    public String generateContent(ActivityEvent activityEvent) {
        return null;
    }


    @Override
    public String generateRouter(ActivityEvent activityEvent) {
        Long userId = activityEvent.getTargetId();
        if (userId != null) {
            return "/user/" + userId;
        }
        return "/error";
    }

    @Override
    public ActivityEventEnum getActivityEventType() {
        return ActivityEventEnum.USER_FOLLOW;
    }

    @Override
    public String generateTitle(ActivityEvent activityEvent) {
        return "关注了" + userService.getUserObj(activityEvent.getTargetId()).getUserNickname();
    }

    @Override
    public void saveActivityEventToRedis(Message message) {
        UserDTO userFromLocal = BaseUtil.getUserFromLocal();

        Long userId = message.getUserId();
        Long receiverId = message.getReceiverId();
        Set<Long> idSet = Set.of(userId, receiverId);
        for (long id : idSet) {
            String key = RedisConstant.ACTIVITY_USER + id;
            try {
                if (!userFromLocal.getUserId().equals(id)) {
                    message.setMessageTitle("关注了我");
                    message.setMessageRedirect("/user/" + userId);
                }
                RedisUtils.zAdd(key, JSONUtil.toJsonStr(message), message.getMessageUpdateTime().getTime());
            } catch (Exception e) {
                log.error("保存活动事件出错", e);
            }
        }
    }
}
