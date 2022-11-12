package com.chen.LeoBlog.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chen.LeoBlog.base.ResultInfo;
import com.chen.LeoBlog.mapper.ChatConnectionMapper;
import com.chen.LeoBlog.po.ChatConnection;
import com.chen.LeoBlog.po.ChatRecord;
import com.chen.LeoBlog.po.User;
import com.chen.LeoBlog.service.ChatConnectionService;
import com.chen.LeoBlog.service.ChatRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.chen.LeoBlog.constant.RedisConstant.CHAT_USER_LIST;

/**
 * @author 1
 * @description 针对表【lb_chat_connection(聊天对象列表)】的数据库操作Service实现
 * @createDate 2022-10-14 17:35:54
 */
@Service
@Slf4j
public class ChatConnectionServiceImpl extends ServiceImpl<ChatConnectionMapper, ChatConnection>
        implements ChatConnectionService {
    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private ChatRecordService chatRecordService;
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public ResultInfo getChatConnectionList(Long userId) {
        String key = CHAT_USER_LIST + userId;
        Set<String> members = redisTemplate.opsForZSet().reverseRange(key, 0, -1);
        List<ChatConnection> list;
        List<Long> ids = new ArrayList<>();
//        ids.add(1L);
        assert members != null;
        if (members.size() != 0) {
            List<ChatConnection> list1 = query().eq("user_id", userId)
                    .or()
                    .eq("chat_user_id", userId).orderByDesc("chat_last_time").list();
            if (list1.size() != members.size()) {
                members = new HashSet<>();
            } else {
                for (String member : members) {
                    ids.add(Long.parseLong(member));
                }
            }

        }

        if (members.size() == 0) {
            //拿到所有的聊天对象
            list = query().eq("user_id", userId)
                    .or()
                    .eq("chat_user_id", userId).orderByDesc("chat_last_time").list();

            list.forEach(chatConnection -> {
                if (!Objects.equals(chatConnection.getUserId(), userId)) {
                    Long chatUserId = chatConnection.getUserId();
                    chatConnection.setUserId(userId);
                    chatConnection.setChatUserId(chatUserId);
                }
            });
            //排除list中chatUserId为-1的（聊天室）
            list.removeIf(chatConnection -> chatConnection.getChatUserId() == -1);
            list.forEach(user -> redisTemplate.opsForZSet().add(key, user.getChatUserId().toString(), user.getChatLastTime().getTime()));
            ids.addAll(list.stream().map(ChatConnection::getChatUserId).toList());
            if (ids.size() == 0) {
                ids.add(1L);
                redisTemplate.opsForZSet().add(key, "1", new Date().getTime());
//                return ResultInfo.success("暂无聊天对象，快去找人吧");
            }

        }
        ids.add(0, -1L);

        //将ids的方括号去掉
        String idsStr = ids.toString().substring(1, ids.toString().length() - 1);

        //根据聊天对象的id拿到聊天对象的信息，以及最近的聊天记录
        List<User> users = userService.query()
                .in("user_id", ids).last("order by field(user_id," + idsStr + ")").list();

        List<ChatRecord> records = chatRecordService.getChatRecordLastList(userId, ids);
        List<Object> chats = new ArrayList<>();
        //将users和records合并
        for (int i = 0; i < users.size(); i++) {
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("user", users.get(i));
            hashMap.put("record", records.get(i));
            chats.add(hashMap);
        }
        return ResultInfo.success(chats);
    }

    @Override
    public ResultInfo connect(Long userId, Long talkToId) {
        if (Objects.equals(talkToId, userId)) return ResultInfo.fail("不能和自己聊天");
//        log.info("我{}正在和用户{}聊天",userId,talkToId);

//        UserDto userDto = Local.getUser();
//        if(userDto==null) return ResultInfo.fail("请先登录");
//        Long userId = userDto.getUserId();
        List<ChatRecord> list;
        if (talkToId == -1) {
            list = chatRecordService.query()
                    .eq("receiver_id", talkToId)
                    .orderByAsc("record_update_time").list();
        } else {

            List<ChatConnection> connectionList = query().eq("user_id", userId)
                    .eq("chat_user_id", talkToId)
                    .or()
                    .eq("user_id", talkToId)
                    .eq("chat_user_id", userId).orderByDesc("chat_last_time").list();
            if (connectionList.size() == 0) {
                ChatConnection chatConnection = new ChatConnection();
                chatConnection.setUserId(userId);
                chatConnection.setChatUserId(talkToId);
                chatConnection.setChatLastTime(new Date());
                save(chatConnection);
            } else {
                ChatConnection chatConnection = connectionList.get(0);
                chatConnection.setChatLastTime(new Date());
                updateById(chatConnection);
            }
            redisTemplate.opsForZSet().add(CHAT_USER_LIST + userId, talkToId.toString(), new Date().getTime());


            //查询
            list = chatRecordService.query()
                    .eq("user_id", userId)
                    .eq("receiver_id", talkToId)
                    .or()
                    .eq("user_id", talkToId)
                    .eq("receiver_id", userId)
                    .orderByDesc("record_update_time").last("limit 50").list();
            list = CollectionUtil.reverse(list);
            if (talkToId == 1) {
                if (list.size() == 0) {
                    ChatRecord chatRecord = new ChatRecord();
                    chatRecord.setUserId(talkToId);
                    chatRecord.setReceiverId(userId);
                    chatRecord.setRecordContent("你好，我是LeoBlog的机器人，有什么问题可以问我哦");
                    chatRecord.setRecordUpdateTime(new Date());
                    chatRecordService.save(chatRecord);
                    list.add(chatRecord);
                }
            }
        }
        return ResultInfo.success(list);
    }
}




