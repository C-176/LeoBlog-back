package com.chen.LeoBlog.service.impl;

import cn.hutool.captcha.LineCaptcha;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.mail.MailUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chen.LeoBlog.base.CodeSender;
import com.chen.LeoBlog.base.Local;
import com.chen.LeoBlog.base.ResultInfo;
import com.chen.LeoBlog.constant.RedisConstant;
import com.chen.LeoBlog.dto.UserDto;
import com.chen.LeoBlog.mapper.UserMapper;
import com.chen.LeoBlog.po.User;
import com.chen.LeoBlog.service.UserService;
import com.chen.LeoBlog.utils.IdUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author 1
 * @description 针对表【lb_user】的数据库操作Service实现
 * @createDate 2022-10-14 17:36:34
 */
@Service
@Slf4j

public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {
    @Autowired
    private CodeSender codeSender;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private IdUtil idUtil;

    @Override
    public ResultInfo login(Map<String, Object> map, String token, LineCaptcha lineCaptcha) {
        if (token == null || StrUtil.isEmpty(token)) {
            token = getToken();
        }
        //从map中取出用户名和密码，验证码，手机号，邮箱
        String username = (String) map.get("userName");
        String password = (String) map.get("userPassword");
        String captcha = (String) map.get("captcha");
        String phone = (String) map.getOrDefault("userPhone", null);
        String email = (String) map.getOrDefault("userEmail", null);
        if (StrUtil.isBlank(captcha)) {
            return ResultInfo.fail("验证码不可为空");
        }
        if (!lineCaptcha.verify(captcha)) return ResultInfo.fail("验证码错误");

        //判断用户名和密码是否为空
        if (StrUtil.isBlank(password)) return ResultInfo.fail("密码不能为空");
        User user;
        if (StrUtil.isBlank(username) && StrUtil.isBlank(email)) {
            if (StrUtil.isBlank(phone)) return ResultInfo.fail("手机号、用户名、邮箱不能同时为空");
            log.info("手机号登录[{}]", phone);
            user = query().eq("user_phone", phone).one();
            if (user == null) {
                return ResultInfo.fail("手机号尚未注册");
            }
        }else{
            if (StrUtil.isNotBlank(username)) {
                log.info("用户名登录[{}]", username);
                user = query().eq("user_name", username).one();
                if (user == null) return ResultInfo.fail("用户名不存在");
            } else {
                log.info("邮箱登录[{}]", email);
                user = query().eq("user_email", email).one();
                if (user == null) {
                    return ResultInfo.fail("该邮箱尚未注册");
                }
            }
            if (!StrUtil.equals(user.getUserPassword(), password)){
                log.info("密码[{}]错误",password);
                return ResultInfo.fail("密码错误");
            }
        }
        UserDto userDto = new UserDto();
        BeanUtil.copyProperties(user,userDto);
        login2redis(token,user);
        map = new HashMap<>();
        map.put("token", token);
        map.put("user", userDto);
        log.info("登陆成功:token: {}", token);
        return ResultInfo.success(map);
    }

    @Override
    public ResultInfo register(Map<String, Object> map) {
        //从map中取出用户名和密码，验证码，手机号，邮箱
        String userName = (String) map.get("userName");
        String password = (String) map.get("userPassword");
        String captcha = (String) map.get("captcha");
        String phone = (String) map.getOrDefault("userPhone", null);
        String email = (String) map.getOrDefault("userEmail", null);
        String s;
        if (phone == null) {
            s = redisTemplate.opsForValue().get(RedisConstant.USER_CAPTCHA + email);
        } else {
            s = redisTemplate.opsForValue().get(RedisConstant.USER_CAPTCHA + phone);
        }
        if (s == null)  return ResultInfo.fail("验证码已过期");
        if (!StrUtil.equals(s, captcha))  return ResultInfo.fail("验证码错误");

        ResultInfo resultInfo = registerConfirm(userName, phone, email);
        if (resultInfo != null) return resultInfo;

        if (StrUtil.isBlank(email)) {
            if (StrUtil.isBlank(phone)) return ResultInfo.fail("手机号、邮箱不能同时为空");
            log.info("手机号注册[{}]", phone);
        }else{
            log.info("邮箱注册[{}]", email);
        }

        User user = new User();
        user.setUserId(idUtil.nextId("user"));
        user.setUserName(userName);
        user.setUserEmail(email);
        user.setUserPassword(password);
        user.setUserNickname("blogger_"+ RandomUtil.randomString(6));
        user.setUserRegisterDate(new Date());
        // 保存用户
        boolean isSuccess = save(user);
        if (!isSuccess) return ResultInfo.fail("注册失败,请稍后再试");
        String token = getToken();
        //map中存key：token
        map = new HashMap<>();
        map.put("token", token);
        log.info("注册成功:token:{}",token);
        return ResultInfo.success(map);
    }

    @Override
    public ResultInfo confirmPhone(String phone) {
        String captcha = codeSender.send(phone);
        //TODO:向手机号发送验证码
        try {
            MailUtil.send("rtg1999@163.com", "手机验证码测试", "邮件来自LeoBlog\n" + captcha, false);
            log.info("手机验证码发送成功[{}]->[{}]", phone, captcha);
        }catch (Exception e){
            log.error("发送手机验证码失败:->{}",phone,e);
        }
        return ResultInfo.success();
    }

    @Override
    public ResultInfo confirmEmail(String email) {
        String captcha = codeSender.send(email);
        //向邮箱发送验证码
        try {
            MailUtil.send(email,"LeoBlog邮箱验证信息","验证码："+captcha,false);
            log.info("邮箱验证码发送成功[{}]->[{}]",email,captcha);
        }catch (Exception e){
            log.error("发送邮件验证码失败:->{}",email,e);
        }
        return ResultInfo.success();
    }

    @Override
    public ResultInfo getNameByIds(String id) {
        List<User> ids = query().in("user_id", id).list();

        log.info("ids:{}",ids);
        List<User> users = query().in("user_id", ids).last("order by field(user_id," + ids + ")").list();
        //取出每个user的名字，转化为list
        List<Object> names = users.stream().map(User::getUserNickname).collect(Collectors.toList());
        return ResultInfo.success(names);
    }

    private User getUserObj(Long userId){
//        log.info("userId:{}",userId);
        String key = RedisConstant.USER_INFO + userId;
        Object o = redisTemplate.opsForHash().get(key, "user");
        User user;
        //如果redis中没有，就从数据库中取
        if (o != null) {
            user = JSONUtil.toBean(o.toString(),User.class);
//            log.info("从redis中取出user:{}",user);
        } else {

            user = query().eq("user_id", userId).one();
            if (user == null) return null;
            redisTemplate.opsForHash().put(key, "user", JSONUtil.toJsonStr(user));
            redisTemplate.expire(key, RedisConstant.USER_INFO_TTL, TimeUnit.DAYS);
        }
        return user;
    }


    @Override
    public ResultInfo getUser(Long userId) {
        User user = getUserObj(userId);
        if (user == null) return ResultInfo.fail("用户不存在");
        UserDto userDto = new UserDto();
        BeanUtil.copyProperties(user,userDto);
        return ResultInfo.success(userDto);
    }

    @Override
    public ResultInfo getSecurityUser(Long userId) {
        User user = getUserObj(userId);
        if (user == null) return ResultInfo.fail("用户不存在");
        return ResultInfo.success(user);
    }

    @Override
    public ResultInfo updateSecurityUser(Map<String, Object> map, Long userId) {
        String key = RedisConstant.USER_INFO + userId;

//        从map中取出密码
        String userPassword = (String) map.getOrDefault("userPassword",null);
        //取出邮箱
        String userEmail = (String) map.getOrDefault("userEmail",null);
        //取出手机号
        String userPhone = (String) map.getOrDefault("userPhone",null);
        //取出验证码
        String captcha = (String) map.getOrDefault("captcha",null);
        //如果密码不为空，其他两者为空的话
        if (StrUtil.isNotBlank(userPassword) && StrUtil.isBlank(userEmail) && StrUtil.isBlank(userPhone)){
            //更新密码
            try {
                update().set("user_password", userPassword).eq("user_id", userId).update();
                redisTemplate.opsForHash().delete(key, "user");
            }catch (Exception e){
                log.error("更新密码失败:->{}",userId,e);
                return ResultInfo.fail("更新密码失败");
            }

        }else if (StrUtil.isNotBlank(userEmail) && StrUtil.isBlank(userPassword) && StrUtil.isBlank(userPhone)){
            log.info("验证码：{},传入：{}",CodeSender.getCode(),captcha);
            boolean isCorrect = CodeSender.confirmCode(captcha);
            if (!isCorrect) return ResultInfo.fail("验证码错误");
            //更新邮箱
            try {
                //查询邮箱是否已经被注册，不区分大小写
                List<User> user_email = query().eq("user_email", userEmail).list();
                if (user_email.size() > 0) return ResultInfo.fail("邮箱已被注册");
                update().set("user_email", userEmail).eq("user_id", userId).update();
                redisTemplate.opsForHash().delete(key, "user");
            }catch (Exception e){
                log.error("更新邮箱失败:->{}",userId,e);
                return ResultInfo.fail("更新邮箱失败");
            }
        }else if (StrUtil.isNotBlank(userPhone) && StrUtil.isBlank(userPassword) && StrUtil.isBlank(userEmail)){
            log.info("验证码：{},传入：{}",CodeSender.getCode(),captcha);
            boolean isCorrect = CodeSender.confirmCode(captcha);
            if (!isCorrect) return ResultInfo.fail("验证码错误");
            //更新手机号
            try {
                //查询手机号是否已经被注册，不区分大小写
                List<User> user_phone = query().eq("user_phone", userPhone).list();
                if (user_phone.size() > 0) return ResultInfo.fail("手机号已被注册");
                update().set("user_phone", userPhone).eq("user_id", userId).update();
                redisTemplate.opsForHash().delete(key, "user");
            }catch (Exception e){
                log.error("更新手机号失败:->{}",userId,e);
                return ResultInfo.fail("更新手机号失败");
            }
        }

        return ResultInfo.success("更新成功");
    }

    @Override
    public ResultInfo deleteUser(Long userId) {
        //TODO:如果是管理员，可以删除用户，否则只能删除自己

        log.info("删除用户:{}",userId);
        User user = query().eq("user_id", userId).one();
        if (user == null) return ResultInfo.fail("用户不存在");
        return null;
    }

    @Override
    public ResultInfo updateUser(User user) {
        
        boolean isSuccess = update().eq("user_id", user.getUserId()).update(user);
        if (isSuccess) {
            //用户信息更改，清除redis中的缓存
            String key = RedisConstant.USER_INFO + user.getUserId();
            redisTemplate.opsForHash().delete(key, "user");
            return ResultInfo.success("更新成功");
        }
        return ResultInfo.fail("更新失败");
    }

    private ResultInfo registerConfirm(String userName, String phone, String userEmail) {
        User user;
        if (StrUtil.isNotBlank(userEmail)) {
            user = query().eq("user_email", userEmail).one();
            if (user != null) return ResultInfo.fail("该邮箱已注册,可直接登陆");
        }
        if (StrUtil.isNotBlank(phone)) {
            user = query().eq("user_phone", phone).one();
            if (user != null) return ResultInfo.fail("该手机号已注册，可直接登陆");
        }
        user = query().eq("user_name", userName).one();
        if (user != null) return ResultInfo.fail("该用户名已被注册");

        return null;
    }

    private String getToken() {
        return UUID.randomUUID(true).toString();
    }

    private void login2redis(String token, User user) {
        UserDto userDto = new UserDto();
        BeanUtil.copyProperties(user,userDto);
        Local.saveUser(userDto);
        redisTemplate.opsForHash().put(RedisConstant.USER_LOGIN+token, "user", JSONUtil.toJsonStr(user));
    }
}




