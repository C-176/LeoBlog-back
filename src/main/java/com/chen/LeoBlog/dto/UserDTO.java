package com.chen.LeoBlog.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class UserDTO implements Serializable {

    private Long userId;
    private String userName;
    private String userNickname;

    private String userProfilePhoto;
    /**
     * 用户个人介绍
     */
    private String userIntro;

    /**
     * 用户性别。0：女，1：男。
     */
    private Integer userSex;

    /**
     * 用户所在地
     */
    private String userPos;

    /**
     * 用户生日
     */
    private Date userBirthday;

    /**
     * 用户认证
     */
    private String userCertification;

    /**
     * 用户教育情况
     */
    private String userEducation;

    /**
     * 用户行业
     */
    private String userIndustry;
    private String userBgPic;
    private Date userRegisterDate;
    // 用户ip
    private String IP;
    // 用户角色
    private Integer roleId;
}
