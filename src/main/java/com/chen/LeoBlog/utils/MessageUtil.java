package com.chen.LeoBlog.utils;

import com.chen.LeoBlog.base.ResultInfo;
import com.chen.LeoBlog.po.User;
import com.chen.LeoBlog.service.UserService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class MessageUtil {

    @Resource
    private UserService userService;



    // 发文章
    public String getArticleMessage(String userNickname, String title) {
//        User userObj = userService.getUserObj(userId);
//        return userNickname + "发表了文章《" + title + "》";
        return userNickname + "发表了文章《" + title + "》";
    }

    // 点赞
    public String getLikeMessage(String userNickname, String title) {
        return userNickname + "点赞了你的文章《" + title + "》";
    }


    // 评论
    public String getCommentMessage(String userNickname, String title) {
        return userNickname + "评论了你的文章《" + title + "》";
    }
    //    收藏

    public String getCollectMessage(String userNickname, String title) {
        return userNickname + "收藏了你的文章《" + title + "》";
    }

    //    关注
    public String getFollowMessage(String userNickname) {
        return userNickname + "关注了你";
    }

    //    回复
    public String getReplyMessage(String userNickname, String title) {
        return userNickname + "回复了你的文章《" + title + "》";
    }
////    系统消息
//    public String getSystemMessage(Long userId) {
//        User userObj = userService.getUserObj(userId);
//        return userObj.getUserNickname() + "给你发了一条系统消息";
//    }

}
