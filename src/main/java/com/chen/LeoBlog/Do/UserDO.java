package com.chen.LeoBlog.Do;

import lombok.Data;

@Data
public class UserDO {
    private String userName;
    private String userPassword;
    private String captcha;
    private String userPhone;
    private String userEmail;

}
