package com.chen.LeoBlog.kafka.consumer;

import cn.hutool.json.JSONUtil;
import com.chen.LeoBlog.activityEvent.Activity;
import com.chen.LeoBlog.activityEvent.ActivityHandlerFactory;
import com.chen.LeoBlog.constant.RedisConstant;
import com.chen.LeoBlog.utils.RedisUtils;
import com.chen.LeoBlog.websocket.SocketService;
import com.chen.LeoBlog.websocket.enums.WebScoketDataEnum;
import com.chen.LeoBlog.websocket.vo.WebSocketData;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.websocket.Session;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static com.chen.LeoBlog.listener.ActivityEventListener.ACTIVITY_TOPIC;
import static com.chen.LeoBlog.listener.SendMessageEventListener.MESSAGE_TOPIC;

@Component
@Slf4j
public class KafkaConsumer {
    public static final String ACTIVITY_CONSUME_GROUP = "activity-group";
    //    @Value("${spring.kafka.consumer.message-group}")
    private static final String MESSAGE_CONSUME_GROUP1 = "message-group1";
    private static final String MESSAGE_CONSUME_GROUP2 = "message-group2";

    @Resource
    private SocketService socketService;

    @KafkaListener(topics = {ACTIVITY_TOPIC}, groupId = ACTIVITY_CONSUME_GROUP)
    public void consumeActivity(ConsumerRecords<String, String> records, Acknowledgment ack) {
        Iterable<ConsumerRecord<String, String>> records1 = records.records(ACTIVITY_TOPIC);
        try {
            for (ConsumerRecord<String, String> record : records1) {
                String value = record.value();
                Activity activity = JSONUtil.toBean(value, Activity.class);
                ActivityHandlerFactory.execute(activity);
                ack.acknowledge();
            }
        } catch (Exception e) {
            log.error("消费异常！", e);
        }
    }

    @KafkaListener(topics = {MESSAGE_TOPIC})
    public void consumeMessage(ConsumerRecords<String, String> records, Acknowledgment ack) {
        Iterable<ConsumerRecord<String, String>> records1 = records.records(MESSAGE_TOPIC);
        Map<Long, Session> sessionMap = socketService.getSessionMap();
        try {
            for (ConsumerRecord<String, String> record : records1) {
                String value = record.value();
                WebSocketData webSocketData = JSONUtil.toBean(value, WebSocketData.class);
                Integer type = webSocketData.getType();
                if (Objects.equals(type, WebScoketDataEnum.GROUP_CHAT.getType())) {
                    Set<String> ids = RedisUtils.zRange(RedisConstant.ONLINE_ALL, 0, -1);
                    if (ids != null) {
                        ids.forEach(x -> {
                            Long id = Long.valueOf(x);
                            send(sessionMap, id, webSocketData);
                        });
                    }
                } else {
                    Long receiveId = webSocketData.getReceiveId();
                    send(sessionMap, receiveId, webSocketData);
                }
                ack.acknowledge();
            }
        } catch (Exception e) {
            log.error("消费异常！", e);
        }
    }

    public void send(Map<Long, Session> sessionMap, Long receiveId, WebSocketData webSocketData) {
        if (sessionMap.containsKey(receiveId)) {
            try {
                log.info("发送消息：{}", webSocketData);
                socketService.sendToSession(sessionMap.get(receiveId), webSocketData);
            } catch (Exception e) {
                log.error("发送session消息失败！", e);
            }
        }
    }

}
