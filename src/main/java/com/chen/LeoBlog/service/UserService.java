package com.chen.LeoBlog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chen.LeoBlog.base.ResultInfo;
import com.chen.LeoBlog.dto.UserLoginOrRegisterDTO;
import com.chen.LeoBlog.po.User;

import java.util.Map;

/**
 * @author 1
 * @description 针对表【lb_user】的数据库操作Service
 * @createDate 2022-10-14 17:36:34
 */
public interface UserService extends IService<User> {

    ResultInfo login(UserLoginOrRegisterDTO user);

    ResultInfo register(UserLoginOrRegisterDTO userLoginOrRegisterDTO);

    ResultInfo confirmPhone(String phone);

    void confirmEmail(String email);

    ResultInfo getNameByIds(String ids);

    ResultInfo getUser(Long userId);

    ResultInfo deleteUser(Long userId);

    ResultInfo updateUser(User user);

    ResultInfo getSecurityUser(Long userId);

    ResultInfo updateSecurityUser(UserLoginOrRegisterDTO userLoginOrRegisterDTO);

    ResultInfo changePwd(Map<String, Object> map);

    ResultInfo followUser(Long followId);

    ResultInfo getFollowStatus(Long followId);

    ResultInfo getCommonFollow(Long userId);

    ResultInfo getFans();

    ResultInfo getFollowed();

    ResultInfo unfollowUser(Long followId);

    User getUserObj(Long userId);

    ResultInfo logout();
}
