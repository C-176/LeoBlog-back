package com.chen.LeoBlog.controller;

import cn.hutool.captcha.LineCaptcha;
import cn.hutool.core.util.StrUtil;
import com.chen.LeoBlog.annotation.Anonymous;
import com.chen.LeoBlog.base.ResultInfo;
import com.chen.LeoBlog.dto.UserLoginOrRegisterDTO;
import com.chen.LeoBlog.po.User;
import com.chen.LeoBlog.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@RestController
@Slf4j
@CrossOrigin
@RequestMapping("/user")
public class UserController {
    @Resource
    private UserService userService;
    @Resource
    private LineCaptcha lineCaptcha;

    @Anonymous
    @PostMapping("/login")
    public ResultInfo login(@RequestBody UserLoginOrRegisterDTO user) {
        return userService.login(user);
    }

    @Anonymous
    @PostMapping("/register")
    public ResultInfo register(@RequestBody UserLoginOrRegisterDTO userLoginOrRegisterDTO) {
        return userService.register(userLoginOrRegisterDTO);
    }

    @Anonymous
    //发送验证码
    @GetMapping("/confirm/phone/{phone}")
    public ResultInfo confirmPhone(@PathVariable("phone") String phone) {
        return userService.confirmPhone(phone);
    }

    @Anonymous
    //发送验证码
    @GetMapping("/confirm/email/{email}")
    public void confirmEmail(@PathVariable("email") String email) {
        userService.confirmEmail(email);
    }

    @Anonymous
    @PostMapping("/changePwd")
    public ResultInfo changePwd(@RequestBody Map<String, Object> map) {
        return userService.changePwd(map);
    }

    @Anonymous
    @RequestMapping("/getCaptcha")

    public void getCaptcha(HttpServletResponse response) {
        StrUtil.format("Captcha: {}", lineCaptcha.getCode());
        response.setContentType("image/jpeg");
        response.setHeader("Pragma", "No-cache");
        try (ServletOutputStream outputStream = response.getOutputStream()) {
            lineCaptcha.write(outputStream);
        } catch (IOException e) {
            log.error("图片验证码加载失败", e);
        }
    }

    @Anonymous
    @GetMapping("/name")
    public ResultInfo getNameByIds(@RequestParam(value = "ids") String ids) {
        log.info("ids:{}", ids);
        return userService.getNameByIds(ids);
    }

    @Anonymous
    @GetMapping("/{userId}")
    public ResultInfo getUser(@PathVariable("userId") Long userId) {
        return userService.getUser(userId);
    }

    @GetMapping("/security/{userId}")
    public ResultInfo getSecurityUser(@PathVariable("userId") Long userId) {
        return userService.getSecurityUser(userId);
    }

    @DeleteMapping("/{userId}")
    public ResultInfo deleteUser(@PathVariable("userId") Long userId) {
        return userService.deleteUser(userId);
    }

    @PutMapping("/update")
    public ResultInfo updateUser(@RequestBody User user) {
        return userService.updateUser(user);
    }

    @PutMapping("/security")
    public ResultInfo updateSecurityUser(@RequestBody UserLoginOrRegisterDTO userLoginOrRegisterDTO) {
        return userService.updateSecurityUser(userLoginOrRegisterDTO);
    }

    // 关注

    @GetMapping("/follow/{followId}")
    public ResultInfo followUser(@PathVariable("followId") Long followId) {
        return userService.followUser(followId);
    }

    // 取消关注
    @GetMapping("/unfollow/{followId}")
    public ResultInfo unfollowUser(@PathVariable("followId") Long followId) {
        return userService.unfollowUser(followId);
    }

    // 获取关注列表
    @GetMapping("/followed")
    public ResultInfo getFollowed() {
        return userService.getFollowed();
    }

    // 获取粉丝列表
    @GetMapping("/fans")
    public ResultInfo getFans() {
        return userService.getFans();
    }

    // 获取关注状态
    @GetMapping("/followStatus/{followId}")
    public ResultInfo getFollowStatus(@PathVariable("followId") Long followId) {
        return userService.getFollowStatus(followId);
    }

    // 获取共同关注列表
    @GetMapping("/commonFollow/{userId}")
    public ResultInfo getCommonFollow(@PathVariable("userId") Long userId) {
        return userService.getCommonFollow(userId);
    }

    @GetMapping("/logout")
    public ResultInfo logout() {
        return userService.logout();
    }


}