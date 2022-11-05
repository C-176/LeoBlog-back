package com.chen.LeoBlog.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName lb_user
 */
@TableName(value ="lb_user")
@Data
public class User implements Serializable {
    /**
     * 用户id
     */
    @TableId
    private Long userId;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 用户email
     */
    private String userEmail;

    /**
     * 用户手机号
     */
    private String userPhone;

    /**
     * 用户密码
     */
    private String userPassword;

    /**
     * 用户昵称
     */
    private String userNickname;

    /**
     * 用户头像
     */
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

    /**
     * 注册日期
     */
    private Date userRegisterDate;

    /**
     * 背景图
     */
    private String userBgPic;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}