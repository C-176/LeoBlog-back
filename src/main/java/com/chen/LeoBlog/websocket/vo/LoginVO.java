package com.chen.LeoBlog.websocket.vo;

import com.chen.LeoBlog.dto.UserDTO;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginVO {
    private String accessToken;
    private String refreshToken;
    private UserDTO user;

}
