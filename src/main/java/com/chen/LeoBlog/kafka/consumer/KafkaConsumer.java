package com.chen.LeoBlog.kafka.consumer;

import cn.hutool.json.JSONUtil;
import com.chen.LeoBlog.activityEvent.Activity;
import com.chen.LeoBlog.activityEvent.ActivityHandlerFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import static com.chen.LeoBlog.listener.ActivityEventListener.ACTIVITY_TOPIC;

@Component
@Slf4j
public class KafkaConsumer {
    public static final String ACTIVITY_CONSUME_GROUP = "activity-group";

    @KafkaListener(topics = {ACTIVITY_TOPIC}, groupId = ACTIVITY_CONSUME_GROUP)
    public void consume(ConsumerRecords<String, String> records, Acknowledgment ack) {
        Iterable<ConsumerRecord<String, String>> records1 = records.records(ACTIVITY_TOPIC);
        try {
            for (ConsumerRecord<String, String> record : records1) {
                String value = record.value();
                Activity activity = JSONUtil.toBean(value, Activity.class);
                ActivityHandlerFactory.execute(activity);
                ack.acknowledge();
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("消费失败");
        }
    }
}
