package com.chen.LeoBlog.dto;

import lombok.Data;

@Data
public class UserLoginOrRegisterDTO {
    private String userName;
    private String userPassword;
    private String captcha;
    private String userPhone;
    private String userEmail;

}
