package com.chen.LeoBlog.service;


public interface KafkaService {

    void sendMessage(String topic, String message);
}
