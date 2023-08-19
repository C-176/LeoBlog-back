package com.chen.LeoBlog.kafka;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/kafka")
public class KafkaController {
//    @Resource
//    private KafkaTemplate<String, String> kafkaTemplate;
//
//    @RequestMapping("/send")
//    public String send() {
//        kafkaTemplate.send("test", 0, null, "hello");
//        return "success";
//
//    }
}
