package com.chen.LeoBlog.websocket.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Setter;

@Data
@Setter
@AllArgsConstructor
public class WebSocketDataWithUserId extends WebSocketData {
    private Long userId;
}
