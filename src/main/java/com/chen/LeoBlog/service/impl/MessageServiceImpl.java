package com.chen.LeoBlog.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chen.LeoBlog.base.ResultInfo;
import com.chen.LeoBlog.constant.RedisConstant;
import com.chen.LeoBlog.mapper.MessageMapper;
import com.chen.LeoBlog.po.Message;
import com.chen.LeoBlog.service.MessageService;
import com.chen.LeoBlog.utils.BaseUtil;
import com.chen.LeoBlog.utils.CursorUtils;
import com.chen.LeoBlog.utils.RedisUtil;
import com.chen.LeoBlog.vo.request.CursorPageBaseReqWithUserId;
import com.chen.LeoBlog.vo.response.CursorPageBaseResp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;

/**
 * @author 1
 * @description 针对表【lb_message】的数据库操作Service实现
 * @createDate 2022-11-11 12:08:06
 */
@Service
@Slf4j
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message>
        implements MessageService {

    @Resource
    private MessageMapper messageMapper;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Resource
    private RedisUtil redisUtil;
    @Resource
    private CursorUtils cursorUtils;


    @Override
    public ResultInfo getMsgByUserId(Long userId, Integer page, Integer size) {
        log.info("getMsgByUserId:userId={},page={},size={}", userId, page, size);
        try {
            Page<Message> pageObj = new Page<>(page, size);
            messageMapper.selectPage(pageObj, new QueryChainWrapper<>(messageMapper).eq("receiver_id", userId).orderByDesc("message_update_time").getWrapper());
            return ResultInfo.success(pageObj);
        } catch (Exception e) {
            log.error("查询消息失败[{}]", userId, e);
        }

        return ResultInfo.success(new ArrayList<Message>());
    }

    @Override
    public boolean addMessage(Message message) {
        try {
//            Message message = new Message();
//            message.setMessageTitle((String) map.get("messageTitle"));
//            message.setMessageContent((String) map.get("messageContent"));
//            message.setMessageUpdateTime(new Date());
//            message.setMessageType((Integer) map.getOrDefault("messageType", 0));
//            message.setReceiverId(Long.parseLong(map.get("receiverId").toString()));
//            message.setUserId(Long.parseLong(map.get("userId").toString()));
            save(message);
            return true;
        } catch (Exception e) {
            log.error("添加消息失败", e);
        }
        return false;
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


    @Override
    public ResultInfo readMessage(Long messageId) {
        boolean isSuccess = update().eq("message_id", messageId).set("isSaw", 1).update();
        if (isSuccess) return ResultInfo.success();
        else return ResultInfo.fail("已读失败");
    }

    @Override
    public ResultInfo getMsgFromBox(CursorPageBaseReqWithUserId cursorPageBaseReq) {
        Long userId = cursorPageBaseReq.getUserId();
        if (cursorPageBaseReq.getUserId() == null) {
            userId = BaseUtil.getUserFromLocal().getUserId();
        }
        if (cursorPageBaseReq.getCursor() == null)
            cursorPageBaseReq.setCursor(System.currentTimeMillis() + "");
        String messageBox = RedisConstant.ACTIVITY_USER + userId;
        CursorPageBaseResp<?> cursorPageByRedis = cursorUtils.getCursorPageByRedis(cursorPageBaseReq, messageBox, x -> JSONUtil.toBean(x, Message.class), false);
        return ResultInfo.success(cursorPageByRedis);
    }

    @Override
    public ResultInfo<?> getActivity(CursorPageBaseReqWithUserId cursorPageBaseReq) {
        Long userId = cursorPageBaseReq.getUserId();
        if (cursorPageBaseReq.getUserId() == null) {
            userId = BaseUtil.getUserFromLocal().getUserId();
        }
        if (cursorPageBaseReq.getCursor() == null)
            cursorPageBaseReq.setCursor(System.currentTimeMillis() + "");
        String messageBox = RedisConstant.ACTIVITY_USER + userId;
        CursorPageBaseResp<?> cursorPageByRedis = cursorUtils.getCursorPageByRedis(cursorPageBaseReq, messageBox, x -> JSONUtil.toBean(x, Message.class), false);
        return ResultInfo.success(cursorPageByRedis);
    }
}




