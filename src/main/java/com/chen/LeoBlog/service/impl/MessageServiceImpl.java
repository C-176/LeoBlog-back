package com.chen.LeoBlog.service.impl;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chen.LeoBlog.base.ResultInfo;
import com.chen.LeoBlog.constant.RedisConstant;
import com.chen.LeoBlog.dto.UserDTO;
import com.chen.LeoBlog.mapper.MessageMapper;
import com.chen.LeoBlog.po.Message;
import com.chen.LeoBlog.service.MessageService;
import com.chen.LeoBlog.utils.BaseUtil;
import com.chen.LeoBlog.utils.RedisUtil;
import com.chen.LeoBlog.vo.request.CursorPageBaseReqWithUserId;
import com.chen.LeoBlog.vo.response.CursorPageBaseResp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

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
    public ResultInfo getMsgFromBox(Integer offset, Long lastScore) {
        UserDTO user = BaseUtil.getUserFromLocal();
        int count = 10;
        String messageBox = RedisConstant.MESSAGE_BOX_PREFIX + user.getUserId();
        // 取出所有的文章id
        Set<ZSetOperations.TypedTuple<String>> typedTuples = redisTemplate.opsForZSet()
                .reverseRangeByScoreWithScores(messageBox, 0, lastScore, offset, count);
        if (typedTuples == null || typedTuples.isEmpty()) {
            return ResultInfo.success(Map.of("messages", Collections.emptyList(), "lastScore", System.currentTimeMillis(), "offset", 0));
        }
        // 转化为Long
        Double score = RandomUtil.randomDouble();
        // 重置偏移量
        offset = 1;
        List<Long> msgIds = new ArrayList<>();
        int originalSize = typedTuples.size();

        for (ZSetOperations.TypedTuple<String> typedTuple : typedTuples) {
            assert score != null;
            // 计算当前查到数据中最后一个分数的重复个数
            if (score.equals(typedTuple.getScore())) {
                offset++;
            } else { // 如果不相等，说明已经到了下一个分数的数据，重置偏移量
                score = typedTuple.getScore();
                offset = 1;
            }
            long value = Long.parseLong(Objects.requireNonNull(typedTuple.getValue()));
            msgIds.add(value);
        }
        String ids = StrUtil.join(",", msgIds);
        // 查询，并且保证顺序
        //无需担心文章被删除，因为文章被删除后，就查不出对应的文章。
        List<Message> messages = query().in("message_id", msgIds).last("order by field(message_id," + ids + ")").list();
        Set<Long> existIds = messages.stream().map(Message::getMessageId).collect(Collectors.toSet());
        // 如果查询出来的文章id和redis中的id不一致，说明有文章被删除了，需要删除redis中的数据
        if (existIds.size() != originalSize) {
            for (Long id : msgIds) {
                if (!existIds.contains(id)) {
                    redisTemplate.opsForZSet().remove(messageBox, id.toString());
                }
            }
        }

        return ResultInfo.success(Map.of("messages", messages, "offset", offset, "lastScore", score.longValue()));

    }

    @Override
    public ResultInfo<?> getActivity(CursorPageBaseReqWithUserId cursorPageBaseReq) {
        Long userId = cursorPageBaseReq.getUserId();
        if (cursorPageBaseReq.getUserId() == null) {
            userId = BaseUtil.getUserFromLocal().getUserId();
        }
        if (cursorPageBaseReq.getCursor() == null)
            cursorPageBaseReq.setCursor(System.currentTimeMillis() + "");
        long lastScore = Long.parseLong(cursorPageBaseReq.getCursor());
        Integer count = cursorPageBaseReq.getPageSize();
        Integer offset = cursorPageBaseReq.getOffset();
        String messageBox = RedisConstant.ACTIVITY_USER + userId;
        // 取出所有的文章id
        Set<ZSetOperations.TypedTuple<String>> typedTuples = redisTemplate.opsForZSet()
                .reverseRangeByScoreWithScores(messageBox, 0, lastScore, offset, count);
        if (typedTuples == null || typedTuples.isEmpty()) {
            return ResultInfo.success(CursorPageBaseResp.of(System.currentTimeMillis() + "", 1, Collections.emptyList(), cursorPageBaseReq.getPageSize()));
        }
        // 转化为Long
        Double score = RandomUtil.randomDouble();
        // 重置偏移量
        offset = 1;
        List<Message> messages = new ArrayList<>();

        for (ZSetOperations.TypedTuple<String> typedTuple : typedTuples) {
            assert score != null;
            // 计算当前查到数据中最后一个分数的重复个数
            if (score.equals(typedTuple.getScore())) {
                offset++;
            } else { // 如果不相等，说明已经到了下一个分数的数据，重置偏移量
                score = typedTuple.getScore();
                offset = 1;
            }
            Message value = JSONUtil.toBean(typedTuple.getValue(), Message.class);
            messages.add(value);
        }
        Collections.reverse(messages);

        return ResultInfo.success(CursorPageBaseResp.of(score.longValue() + "", offset, messages, cursorPageBaseReq.getPageSize()));
    }
}




