package com.chen.LeoBlog.controller;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import cn.hutool.core.util.StrUtil;
import com.chen.LeoBlog.base.ResultInfo;
import com.chen.LeoBlog.po.User;
import com.chen.LeoBlog.service.UserService;
import io.swagger.annotations.ApiOperation;
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

    private LineCaptcha lineCaptcha;

    @PostMapping("/login")
    public ResultInfo login(@RequestBody Map<String, Object> map, @RequestHeader(value = "Authorization", defaultValue = "", required = false) String token) {
        return userService.login(map, token, lineCaptcha);
    }

    @PostMapping("/register")
    public ResultInfo register(@RequestBody Map<String, Object> map) {
        return userService.register(map);
    }

    //发送验证码
    @GetMapping("/confirm/phone/{phone}")
    public ResultInfo confirmPhone(@PathVariable("phone") String phone){
        return userService.confirmPhone(phone);
    }
    //发送验证码
    @GetMapping("/confirm/email/{email}")
    public ResultInfo confirmEmail(@PathVariable("email") String email){
        return userService.confirmEmail(email);
    }

    @PostMapping("/changePwd")
    public ResultInfo changePwd(@RequestBody Map<String, Object> map){
        return userService.changePwd(map);
    }

    @RequestMapping("/getCaptcha")
    public void getCaptcha(HttpServletResponse response) {
        lineCaptcha = CaptchaUtil.createLineCaptcha(116, 36, 4, 20);

        StrUtil.format("Captcha: {}", lineCaptcha.getCode());
        response.setContentType("image/jpeg");
        response.setHeader("Pragma", "No-cache");
        try {
            ServletOutputStream outputStream = response.getOutputStream();
            lineCaptcha.write(outputStream);
            outputStream.close();
        } catch (IOException e) {
            log.error("图片验证码加载失败", e);
        }
    }
    @ApiOperation("根据用户id获取用户名称")
    @GetMapping("/name")
    public ResultInfo getNameByIds(@RequestParam(value ="ids") String ids){
        log.info("ids:{}",ids);
        return userService.getNameByIds(ids);
    }

    @GetMapping("/{userId}")
    public ResultInfo getUser(@PathVariable("userId") Long userId){
        return userService.getUser(userId);
    }

    @GetMapping("/security/{userId}")
    public ResultInfo getSecurityUser(@PathVariable("userId") Long userId){
        return userService.getSecurityUser(userId);
    }

    @DeleteMapping("/{userId}")
    public ResultInfo deleteUser(@PathVariable("userId") Long userId){
        return userService.deleteUser(userId);
    }

    @PutMapping("/update")
    public ResultInfo updateUser(@RequestBody User user){
        return userService.updateUser(user);
    }

    @PutMapping("/security/{userId}")
    public ResultInfo updateSecurityUser(@RequestBody Map<String,Object> map,@PathVariable("userId") Long userId){
        return userService.updateSecurityUser(map,userId);
    }

}