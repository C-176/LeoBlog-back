package com.chen.LeoBlog.publisher;

import com.chen.LeoBlog.activityEvent.Activity;
import com.chen.LeoBlog.activityEvent.ActivityEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component
public class ActivityEventPublisher {
    @Resource
    private ApplicationEventPublisher applicationEventPublisher;


    public void publish(Activity activity) {
        applicationEventPublisher.publishEvent(new ActivityEvent<>(activity));
    }


}
