package com.chen.LeoBlog.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chen.LeoBlog.base.ResultInfo;
import com.chen.LeoBlog.mapper.MessageMapper;
import com.chen.LeoBlog.po.Message;
import com.chen.LeoBlog.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author 1
 * @description 针对表【lb_message】的数据库操作Service实现
 * @createDate 2022-11-11 12:08:06
 */
@Service
@Slf4j
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message>
        implements MessageService {

    @Override
    public ResultInfo getMsgByUserId(Long userId) {
        try {
            List<Message> list = query().eq("receiver_id", userId).orderByDesc("message_update_time").list();
            if (list.size() > 0) {
                return ResultInfo.success(list);
            }
        } catch (Exception e) {
            log.error("查询消息失败[{}]", userId, e);
        }

        return ResultInfo.success(new ArrayList<Message>());
    }

    @Override
    public ResultInfo addMessage(Map<String, Object> map) {
        try {
            Message message = new Message();
            message.setMessageTitle((String) map.get("messageTitle"));
            message.setMessageContent((String) map.get("messageContent"));
            message.setMessageUpdateTime(new Date());
            message.setMessageType((Integer) map.getOrDefault("messageType", 0));
            message.setReceiverId(Long.parseLong(map.get("receiverId").toString()));
            message.setUserId(Long.parseLong(map.get("userId").toString()));
            save(message);
            return ResultInfo.success("添加成功");
        } catch (Exception e) {
            log.error("添加消息失败[{}]", map, e);
        }
        return ResultInfo.fail("添加失败");
    }

    @Override
    public ResultInfo deleteMessage(Long id) {
        try {
            removeById(id);
            return ResultInfo.success("删除成功");
        } catch (Exception e) {
            log.error("删除消息失败[{}]", id, e);
        }
        return ResultInfo.fail("删除失败");
    }
}




