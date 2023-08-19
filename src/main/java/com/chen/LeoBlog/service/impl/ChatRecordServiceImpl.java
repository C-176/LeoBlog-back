package com.chen.LeoBlog.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Pair;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chen.LeoBlog.base.ResultInfo;
import com.chen.LeoBlog.dto.UserDTO;
import com.chen.LeoBlog.mapper.ChatRecordMapper;
import com.chen.LeoBlog.po.ChatRecord;
import com.chen.LeoBlog.service.ChatRecordService;
import com.chen.LeoBlog.utils.BaseUtil;
import com.chen.LeoBlog.utils.CursorUtils;
import com.chen.LeoBlog.vo.request.CursorPageBaseReqWithUserId;
import com.chen.LeoBlog.vo.response.CursorPageBaseResp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author 1
 * @description 针对表【lb_chat_record】的数据库操作Service实现
 * @createDate 2022-10-14 17:35:57
 */
@Service
@Slf4j
public class ChatRecordServiceImpl extends ServiceImpl<ChatRecordMapper, ChatRecord> implements ChatRecordService {
    @Resource
    private ChatRecordMapper chatRecordMapper;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Resource
    private CursorUtils cursorUtils;

    @Override
    public List<ChatRecord> getChatRecordLastList(Long userId, List<Long> ids) {
        List<ChatRecord> records = new ArrayList<>();
        ids.forEach(id -> {
            if (id == -1) {
                records.add(query().eq("receiver_id", -1L).orderByDesc("record_update_time").last("limit 1").one());
            } else {
                ChatRecord one = query().eq("user_id", userId).eq("receiver_id", id).or().eq("user_id", id).eq("receiver_id", userId).orderByDesc("record_update_time").last("limit 1").one();
                records.add(one == null ? new ChatRecord() : one);
            }
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

    @Override
    public ResultInfo<?> getRecordList(Long userId, Long talkToId, Integer page, Integer size) {
        log.debug("获取聊天记录列表,page:{},size:{}", page, size);
        try {
            Page<ChatRecord> pageObj = new Page<>(page, size);
            if (talkToId == -1) {
                chatRecordMapper.selectPage(pageObj, new QueryChainWrapper<>(chatRecordMapper).eq("receiver_Id", talkToId).orderByDesc("record_update_time").getWrapper());
            } else {
                chatRecordMapper.selectPage(pageObj, new QueryChainWrapper<>(chatRecordMapper).eq("user_id", userId).eq("receiver_id", talkToId).or().eq("user_id", talkToId).eq("receiver_id", userId).orderByDesc("record_update_time").getWrapper());
//            if (talkToId == 1) {
//                if (pageObj.getRecords().size() == 0) {
//                    ChatRecord chatRecord = new ChatRecord();
//                    chatRecord.setUserId(talkToId);
//                    chatRecord.setReceiverId(userId);
//                    chatRecord.setRecordContent("你好，我是LeoBlog的机器人，有什么问题可以问我哦");
//                    chatRecord.setRecordUpdateTime(new Date());
//                    saveActivityMessage(chatRecord);
//                    list.add(chatRecord);
//                }
//            }
            }

            // 将pageObj.records逆序
            pageObj.setRecords(CollectionUtil.reverse(pageObj.getRecords()));
            return ResultInfo.success(pageObj);
        } catch (Exception e) {
            log.error("获取聊天记录失败", e);
            return ResultInfo.fail("获取失败，请重试");
        }
    }

    @Override
    public ResultInfo<?> getCursorPage(CursorPageBaseReqWithUserId req) {
        UserDTO userDTO = BaseUtil.getUserFromLocal();
        Long userId = userDTO.getUserId();
        LambdaQueryChainWrapper<ChatRecord> wrapper;
        if (req.getUserId().equals(-1L)) {
            wrapper = lambdaQuery().eq(ChatRecord::getReceiverId, -1L);
        } else {
            wrapper = lambdaQuery().and(i -> i
                    .eq(ChatRecord::getUserId, userId).eq(ChatRecord::getReceiverId, req.getUserId())
                    .or().eq(ChatRecord::getUserId, req.getUserId()).eq(ChatRecord::getReceiverId, userId));
        }
        CursorPageBaseResp<ChatRecord> cursorPageBaseResp = cursorUtils.getCursorPageByMysql(this, req, wrapper, ChatRecord::getRecordId, false);
        return ResultInfo.success(cursorPageBaseResp);
    }

    @Override
    public ResultInfo<?> addExample() {
        // 将-1的聊天记录放入redis
        List<Pair<Long, Date>> receiverId = query().eq("receiver_id", -1L).list().stream().map(x -> Pair.of(x.getRecordId(), x.getRecordUpdateTime())).toList();
        receiverId.forEach(x -> redisTemplate.opsForZSet().add("chat:record:-1", x.getKey() + "", x.getValue().getTime()));

        return ResultInfo.success();
    }

}





