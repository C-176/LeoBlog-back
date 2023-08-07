package com.chen.LeoBlog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chen.LeoBlog.base.ResultInfo;
import com.chen.LeoBlog.po.ChatRecord;
import com.chen.LeoBlog.vo.request.CursorPageBaseReqWithUserId;

import java.util.List;

/**
 * @author 1
 * @description 针对表【lb_chat_record】的数据库操作Service
 * @createDate 2022-10-14 17:35:57
 */
public interface ChatRecordService extends IService<ChatRecord> {


    List<ChatRecord> getChatRecordLastList(Long userId, List<Long> ids);

    ResultInfo deleteRecord(Long recordId);

    ResultInfo getRecordList(Long userId, Long chatToId, Integer page, Integer size);

    ResultInfo<?> getCursorPage(CursorPageBaseReqWithUserId req);

    ResultInfo<?> addExample();
}
