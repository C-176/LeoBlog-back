package com.chen.LeoBlog.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chen.LeoBlog.po.Friend;
import com.chen.LeoBlog.service.FriendService;
import com.chen.LeoBlog.mapper.FriendMapper;
import org.springframework.stereotype.Service;

/**
* @author 1
* @description 针对表【lb_friend】的数据库操作Service实现
* @createDate 2022-10-14 17:36:15
*/
@Service
public class FriendServiceImpl extends ServiceImpl<FriendMapper, Friend>
    implements FriendService{

}




