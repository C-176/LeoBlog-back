package com.chen.LeoBlog.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chen.LeoBlog.base.Local;
import com.chen.LeoBlog.base.ResultInfo;
import com.chen.LeoBlog.po.ChatRecord;
import com.chen.LeoBlog.service.ChatRecordService;
import com.chen.LeoBlog.mapper.ChatRecordMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
* @author 1
* @description 针对表【lb_chat_record】的数据库操作Service实现
* @createDate 2022-10-14 17:35:57
*/
@Service
public class ChatRecordServiceImpl extends ServiceImpl<ChatRecordMapper, ChatRecord>
    implements ChatRecordService{


    @Override
    public List<ChatRecord> getChatRecordLastList(Long userId, List<Long> ids) {
        List<ChatRecord> records = new ArrayList<>();
        ids.forEach(id->{
            ChatRecord one = query().eq("user_id", userId)
                    .eq("receiver_id", id)
                    .or()
                    .eq("user_id", id)
                    .eq("receiver_id", userId)
                    .orderByDesc("record_update_time")
                    .last("limit 1")
                    .one();
            records.add(one==null?new ChatRecord():one);
        });
        return records;
    }

    @Override
    public ResultInfo deleteRecord(Long recordId) {
        try {
            removeById(recordId);
            return ResultInfo.success();
        } catch (Exception e) {
            log.error("删除聊天记录失败", e);
            return ResultInfo.fail("删除失败");
        }

    }
}




