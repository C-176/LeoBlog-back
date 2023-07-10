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
import com.chen.LeoBlog.base.ResultInfo;
import com.chen.LeoBlog.constant.RedisConstant;
import com.chen.LeoBlog.dto.UserDto;
import com.chen.LeoBlog.mapper.UserMapper;
import com.chen.LeoBlog.po.User;
import com.chen.LeoBlog.service.UserService;
import com.chen.LeoBlog.utils.IdUtil;
import com.chen.LeoBlog.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.chen.LeoBlog.constant.BaseConstant.*;
import static com.chen.LeoBlog.utils.BaseUtil.getUserFromLocal;

/**
 * @author 1
 * @description 针对表【lb_user】的数据库操作Service实现
 * @createDate 2022-10-14 17:36:34
 */
@Service
@Slf4j

public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {
    @Resource
    private CodeSender codeSender;
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Resource
    private LineCaptcha lineCaptcha;
    @Resource
    private RedisUtil redisUtil;
    @Resource
    private IdUtil idUtil;

    @Override
    public ResultInfo login(Map<String, Object> map, String token) {
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
        } else {
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
            if (!StrUtil.equals(user.getUserPassword(), password)) {
                log.info("密码[{}]错误", password);
                return ResultInfo.fail("密码错误");
            }
        }
        UserDto userDto = new UserDto();
        BeanUtil.copyProperties(user, userDto);
        redisTemplate.opsForValue().set(RedisConstant.USER_LOGIN + token, JSONUtil.toJsonStr(userDto), RedisConstant.USER_LOGIN_TTL, TimeUnit.DAYS);
        log.info("登陆成功:token: {}", token);
        return ResultInfo.success(Map.of("token", token, "user", userDto));
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
        if (s == null) return ResultInfo.fail("验证码已过期");
        if (!StrUtil.equals(s, captcha)) return ResultInfo.fail("验证码错误");
        // 判断用户名邮箱电话是否已经存在
        ResultInfo resultInfo = registerConfirm(userName, phone, email);
        if (resultInfo != null) return resultInfo;

        if (StrUtil.isBlank(email)) {
            if (StrUtil.isBlank(phone)) return ResultInfo.fail("手机号、邮箱不能同时为空");
            log.info("手机号注册[{}]", phone);
        } else {
            log.info("邮箱注册[{}]", email);
        }

        User user = new User();
        user.setUserId(idUtil.nextId("user"));
        user.setUserName(userName);
        user.setUserEmail(email);
        user.setUserPassword(password);
        user.setUserNickname("lber_" + RandomUtil.randomString(4));
        user.setUserRegisterDate(new Date());
        // 保存用户
        boolean isSuccess = save(user);
        if (!isSuccess) return ResultInfo.fail("注册失败,请稍后再试");
        String token = getToken();
        log.info("注册成功:token:{}", token);
        return ResultInfo.success(Map.of("token", token));
    }

    @Override
    public ResultInfo confirmPhone(String phone) {
        String captcha = codeSender.send(phone);
        //TODO:向手机号发送验证码
        try {
            MailUtil.send("rtg1999@163.com", "手机验证码测试", "邮件来自LeoBlog\n" + captcha, false);
            log.info("手机验证码发送成功[{}]->[{}]", phone, captcha);
        } catch (Exception e) {
            log.error("发送手机验证码失败:->{}", phone, e);
        }
        return ResultInfo.success();
    }

    @Override
    public ResultInfo confirmEmail(String email) {
        String captcha = codeSender.send(email);
        //向邮箱发送验证码
        try {
            MailUtil.send(email, "LeoBlog邮箱验证信息", htmlPrefix + "验证身份" + htmlMiddle + captcha + htmlSuffix, true);
            log.info("邮箱验证码发送成功[{}]->[{}]", email, captcha);
        } catch (Exception e) {
            log.error("发送邮件验证码失败:->{}", email, e);
        }
        return ResultInfo.success();
    }


    @Override
    public ResultInfo getNameByIds(String id) {
        List<User> ids = query().in("user_id", id).list();
        log.info("ids:{}", ids);
        List<User> users = query().in("user_id", ids).last("order by field(user_id," + ids + ")").list();
        //取出每个user的名字，转化为list
        List<Object> names = users.stream().map(User::getUserNickname).collect(Collectors.toList());
        return ResultInfo.success(names);
    }


    @Override
    public ResultInfo getSecurityUser(Long userId) {
        UserDto userDtoObj = getUserDtoObj(userId);
        if (userDtoObj == null) return ResultInfo.fail("用户不存在");
        return ResultInfo.success(userDtoObj);
    }

    @Override
    public ResultInfo getUser(Long userId) {
        User user = getUserObj(userId);
        if (user == null) return ResultInfo.fail("用户不存在");
        return ResultInfo.success(user);
    }

    public User getUserObj(Long userId) {
        return redisUtil.getObjWithCache(RedisConstant.USER_INFO + userId, userId,
                User.class, RedisConstant.USER_INFO_TTL, TimeUnit.DAYS, (uid) -> query().eq("user_id", uid).one());
    }

    public UserDto getUserDtoObj(Long userId) {
        User user = getUserObj(userId);
        if (user == null) return null;
        UserDto userDto = new UserDto();
        BeanUtil.copyProperties(user, userDto);
        return userDto;
    }

    @Override
    public ResultInfo updateSecurityUser(Map<String, Object> map, Long userId) {
        String key = RedisConstant.USER_INFO + userId;
//        从map中取出密码
        String userPassword = (String) map.getOrDefault("userPassword", null);
        //取出邮箱
        String userEmail = (String) map.getOrDefault("userEmail", null);
        //取出手机号
        String userPhone = (String) map.getOrDefault("userPhone", null);
        //取出验证码
        String captcha = (String) map.getOrDefault("captcha", null);
        //如果密码不为空，其他两者为空的话
        if (StrUtil.isNotBlank(userPassword) && StrUtil.isBlank(userEmail) && StrUtil.isBlank(userPhone)) {
            //更新密码
            try {
                update().set("user_password", userPassword).eq("user_id", userId).update();
                redisTemplate.delete(key);
            } catch (Exception e) {
                log.error("更新密码失败:->{}", userId, e);
                return ResultInfo.fail("更新密码失败");
            }

        } else if (StrUtil.isNotBlank(userEmail) && StrUtil.isBlank(userPassword) && StrUtil.isBlank(userPhone)) {
            log.info("验证码：{},传入：{}", CodeSender.getCode(), captcha);
            boolean isCorrect = CodeSender.confirmCode(captcha);
            if (!isCorrect) return ResultInfo.fail("验证码错误");
            //更新邮箱
            try {
                //查询邮箱是否已经被注册，不区分大小写
                List<User> user_email = query().eq("user_email", userEmail).list();
                if (user_email.size() > 0) return ResultInfo.fail("邮箱已被注册");
                update().set("user_email", userEmail).eq("user_id", userId).update();
                redisTemplate.delete(key);
            } catch (Exception e) {
                log.error("更新邮箱失败:->{}", userId, e);
                return ResultInfo.fail("更新邮箱失败");
            }
        } else if (StrUtil.isNotBlank(userPhone) && StrUtil.isBlank(userPassword) && StrUtil.isBlank(userEmail)) {
            log.info("验证码：{},传入：{}", CodeSender.getCode(), captcha);
            boolean isCorrect = CodeSender.confirmCode(captcha);
            if (!isCorrect) return ResultInfo.fail("验证码错误");
            //更新手机号
            try {
                //查询手机号是否已经被注册，不区分大小写
                List<User> user_phone = query().eq("user_phone", userPhone).list();
                if (user_phone.size() > 0) return ResultInfo.fail("手机号已被注册");
                update().set("user_phone", userPhone).eq("user_id", userId).update();
                redisTemplate.delete(key);
            } catch (Exception e) {
                log.error("更新手机号失败:->{}", userId, e);
                return ResultInfo.fail("更新手机号失败");
            }
        }

        return ResultInfo.success("更新成功");
    }

    @Override
    public ResultInfo changePwd(Map<String, Object> map) {
        String userPassword = (String) map.getOrDefault("userPassword", null);
        //取出验证码
        String captcha = (String) map.getOrDefault("captcha", null);
        if (StrUtil.isBlank(userPassword)) return ResultInfo.fail("密码不能为空");

        String phone = (String) map.getOrDefault("userPhone", null);
        String email = (String) map.getOrDefault("userEmail", null);
        String s = redisTemplate.opsForValue().get(RedisConstant.USER_CAPTCHA + (phone == null ? email : phone));
        if (s == null) return ResultInfo.fail("验证码已过期");
        if (!StrUtil.equals(s, captcha)) return ResultInfo.fail("验证码错误");
        User user = redisUtil.getObjWithCache(RedisConstant.USER_INFO + email, email,
                User.class, RedisConstant.USER_INFO_TTL, TimeUnit.DAYS, (ue) -> query().eq("user_email", ue).one());
        if (user == null) user = redisUtil.getObjWithCache(RedisConstant.USER_INFO + phone, phone,
                User.class, RedisConstant.USER_INFO_TTL, TimeUnit.DAYS, (up) -> query().eq("user_phone", up).one());
        if (user == null) return ResultInfo.fail("用户不存在");
        try {
            update().set("user_password", userPassword).eq("user_id", user.getUserId()).update();
        } catch (Exception e) {
            log.error("更新密码失败:->{}", user.getUserId(), e);
            return ResultInfo.fail("更新密码失败");
        }
        return ResultInfo.success("更新成功");
    }

    @Override
    public ResultInfo followUser(Long followId) {
        UserDto user = getUserFromLocal();
        Long userId = user.getUserId();
        if (userId.equals(followId)) return ResultInfo.fail("不能关注自己");
        // 查询是否已经关注
        String followKey = RedisConstant.FOLLOW_USER_LIST + userId;
        String fansKey = RedisConstant.FAN_USER_LIST + followId;
        try {
            Boolean isMember = redisTemplate.opsForSet().isMember(followKey, followId.toString());
            if (Boolean.TRUE.equals(isMember)) return ResultInfo.fail("不能重复关注");
            redisTemplate.opsForSet().add(followKey, followId.toString());
            redisTemplate.opsForSet().add(fansKey, userId.toString());
        } catch (Exception e) {
            log.error("关注失败:->{}", userId, e);
            return ResultInfo.fail("关注失败");
        }
        return ResultInfo.success("关注成功");
    }

    @Override
    public ResultInfo unfollowUser(Long followId) {
        UserDto user = getUserFromLocal();
        Long userId = user.getUserId();
        if (userId.equals(followId)) return ResultInfo.fail("不能取关自己");
        // 查询是否已经关注
        String followKey = RedisConstant.FOLLOW_USER_LIST + userId;
        String fansKey = RedisConstant.FAN_USER_LIST + followId;
        try {
            Boolean isMember = redisTemplate.opsForSet().isMember(followKey, followId.toString());
            if (Boolean.FALSE.equals(isMember)) return ResultInfo.fail("不能取关未关注的用户");
            redisTemplate.opsForSet().remove(followKey, followId.toString());
            redisTemplate.opsForSet().remove(fansKey, userId.toString());
        } catch (Exception e) {
            log.error("取关失败:->{}", userId, e);
            return ResultInfo.fail("取关失败");
        }
        return ResultInfo.success("取关成功");

    }

    @Override
    public ResultInfo getFollowStatus(Long followId) {

        UserDto user = getUserFromLocal();
        String followKey = RedisConstant.FOLLOW_USER_LIST + user.getUserId();
        Boolean isMember = redisTemplate.opsForSet().isMember(followKey, followId.toString());
        return ResultInfo.success(isMember);
    }

    @Override
    public ResultInfo getCommonFollow(Long userId) {
        UserDto user = getUserFromLocal();
        String followKey1 = RedisConstant.FOLLOW_USER_LIST + user.getUserId();
        String followKey2 = RedisConstant.FOLLOW_USER_LIST + userId;
        Set<String> intersect = redisTemplate.opsForSet().intersect(followKey1, followKey2);
        return ResultInfo.success(intersect);
    }

    @Override
    public ResultInfo getFans() {
        UserDto user = getUserFromLocal();
        String fansKey = RedisConstant.FAN_USER_LIST + user.getUserId();
        Set<String> members = redisTemplate.opsForSet().members(fansKey);
        return ResultInfo.success(members);
    }

    @Override
    public ResultInfo getFollowed() {
        UserDto user = getUserFromLocal();
        String followKey = RedisConstant.FOLLOW_USER_LIST + user.getUserId();
        Set<String> members = redisTemplate.opsForSet().members(followKey);
        return ResultInfo.success(members);
    }


    @Override
    public ResultInfo deleteUser(Long userId) {
        //TODO:如果是管理员，可以删除用户，否则只能删除自己
        log.info("删除用户:{}", userId);
        UserDto loginedUser = getUserFromLocal();
        if (!loginedUser.getUserId().equals(userId)) {
            return ResultInfo.fail("无权操作");
        }

        String key = RedisConstant.USER_INFO + userId;
        User user = query().eq("user_id", userId).one();
        if (user == null) return ResultInfo.fail("用户不存在");
        try {
            removeById(userId);
            redisTemplate.delete(key);
        } catch (Exception e) {
            log.error("删除用户失败:->{}", userId, e);
            return ResultInfo.fail("删除失败");
        }
        return ResultInfo.success();
    }

    @Override
    public ResultInfo updateUser(User user) {
        String lockKey = RedisConstant.LOCK_PREFIX + RedisConstant.USER_INFO_TTL + user.getUserId();
        boolean isLock;
        RLock lock;
        try {
            lock = redisUtil.getLock(lockKey);
            isLock = redisUtil.getLock(lockKey) != null;
        } catch (InterruptedException e) {
            log.error("redisson获取锁出错:->{}", user.getUserId(), e);
            throw new RuntimeException(e);
        }
        if (isLock) {
            try {
                boolean isSuccess = update().eq("user_id", user.getUserId()).update(user);
                if (isSuccess) {
                    //用户信息更改，清除redis中的缓存
                    String key = RedisConstant.USER_INFO + user.getUserId();
                    redisTemplate.delete(key);
                    return ResultInfo.success("更新成功");
                }
            } catch (Exception e) {
                log.error("更新用户信息失败:->{}", user.getUserId(), e);
                return ResultInfo.fail("更新失败");
            } finally {
                if (lock != null) redisUtil.releaseLock(lock);
            }
        }
        return ResultInfo.success();
    }

    private ResultInfo registerConfirm(String userName, String phone, String userEmail) {
        User user;
        if (StrUtil.isNotBlank(userEmail)) {
            // 从数据库中查询是否存在该邮箱，不分大小写
            user = redisUtil.getObjWithCache(RedisConstant.USER_INFO + userEmail, userEmail,
                    User.class, RedisConstant.USER_INFO_TTL, TimeUnit.DAYS, (ue) -> query().eq("user_email", ue).one());
            if (user != null) return ResultInfo.fail("该邮箱已被注册");
        }
        if (StrUtil.isNotBlank(phone)) {
            user = redisUtil.getObjWithCache(RedisConstant.USER_INFO + phone, phone,
                    User.class, RedisConstant.USER_INFO_TTL, TimeUnit.DAYS, (up) -> query().eq("user_phone", up).one());
            if (user != null) return ResultInfo.fail("该手机号已被注册");
        }
        user = redisUtil.getObjWithCache(RedisConstant.USER_INFO + userName, userName,
                User.class, RedisConstant.USER_INFO_TTL, TimeUnit.DAYS, (un) -> query().eq("user_name", un).one());
        if (user != null) return ResultInfo.fail("该用户名已被注册");

        return null;
    }

    private String getToken() {
        //生成token
        return UUID.randomUUID(true).toString(true);
    }

    private void login2redis(String token, User user) {
        UserDto userDto = new UserDto();
        BeanUtil.copyProperties(user, userDto);
        redisTemplate.opsForValue().set(RedisConstant.USER_LOGIN + token, JSONUtil.toJsonStr(userDto), RedisConstant.USER_LOGIN_TTL, TimeUnit.DAYS);
    }


}




