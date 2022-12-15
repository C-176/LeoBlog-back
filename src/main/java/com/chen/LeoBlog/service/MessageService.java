package com.chen.LeoBlog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chen.LeoBlog.base.ResultInfo;
import com.chen.LeoBlog.po.Message;

import java.util.Map;

/**
 * @author 1
 * @description 针对表【lb_message】的数据库操作Service
 * @createDate 2022-11-11 12:08:06
 */
public interface MessageService extends IService<Message> {

    ResultInfo getMsgByUserId(Long userId, Integer page, Integer size);

    ResultInfo addMessage(Map<String, Object> map);

    ResultInfo deleteMessage(Long messageId);

    ResultInfo readMessage(Long messageId);
}
