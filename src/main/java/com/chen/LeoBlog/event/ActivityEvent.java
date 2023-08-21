package com.chen.LeoBlog.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEvent;

@Slf4j
public class ActivityEvent<Activity> extends ApplicationEvent {
    public ActivityEvent(Activity message) {
        super(message);
    }
}