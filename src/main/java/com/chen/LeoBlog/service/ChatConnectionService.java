package com.chen.LeoBlog.service;

import com.chen.LeoBlog.base.ResultInfo;
import com.chen.LeoBlog.po.ChatConnection;
import com.baomidou.mybatisplus.extension.service.IService;
import com.chen.LeoBlog.po.User;

import java.util.List;

/**
* @author 1
* @description 针对表【lb_chat_connection(聊天对象列表)】的数据库操作Service
* @createDate 2022-10-14 17:35:54
*/
public interface ChatConnectionService extends IService<ChatConnection> {

    ResultInfo getChatConnectionList(Long userId);

    ResultInfo connect(Long userId,Long talkToId);
}
