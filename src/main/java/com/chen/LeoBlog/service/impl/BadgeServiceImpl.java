package com.chen.LeoBlog.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chen.LeoBlog.activityEvent.Activity;
import com.chen.LeoBlog.activityEvent.ActivityData;
import com.chen.LeoBlog.activityEvent.ActivityEnum;
import com.chen.LeoBlog.annotation.RedissonLock;
import com.chen.LeoBlog.base.ResultInfo;
import com.chen.LeoBlog.base.UserDTOHolder;
import com.chen.LeoBlog.constant.RedisConstant;
import com.chen.LeoBlog.dto.UserDTO;
import com.chen.LeoBlog.mapper.BadgeMapper;
import com.chen.LeoBlog.po.Account;
import com.chen.LeoBlog.po.Badge;
import com.chen.LeoBlog.po.Order;
import com.chen.LeoBlog.po.SetUserBadge;
import com.chen.LeoBlog.publisher.ActivityEventPublisher;
import com.chen.LeoBlog.service.AccountService;
import com.chen.LeoBlog.service.BadgeService;
import com.chen.LeoBlog.service.OrderService;
import com.chen.LeoBlog.service.SetUserBadgeService;
import com.chen.LeoBlog.utils.IdUtil;
import com.chen.LeoBlog.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author rtg19
 * @description 针对表【lb_badge】的数据库操作Service实现
 * @createDate 2023-06-11 18:31:50
 */
@Service
@Slf4j
public class BadgeServiceImpl extends ServiceImpl<BadgeMapper, Badge>
        implements BadgeService {
    @Resource
    private RedisUtil redisUtil;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Resource
    private AccountService accountService;
    @Resource
    private SetUserBadgeService setUserBadgeService;

    @Resource
    private IdUtil idUtil;

    @Resource
    private OrderService orderService;
    @Resource
    private ActivityEventPublisher activityEventPublisher;

    public static final String BUY_LUA_SCRIPT = "buyLimitedBadge.lua";
    private DefaultRedisScript<Long> luaScript;

    @PostConstruct
    public void init() {
        luaScript = new DefaultRedisScript<>();
        luaScript.setLocation(new ClassPathResource(BUY_LUA_SCRIPT));
        luaScript.setResultType(Long.class);
    }


    @Override
    public ResultInfo getBadgeById(String badgeId) {
        // 利用redis缓存取出徽章信息
        String key = RedisConstant.BADGE_INFO + badgeId;
        Badge badge = redisUtil.getObjWithCache(key, Long.parseLong(badgeId), Badge.class, RedisConstant.BADGE_INFO_TTL, TimeUnit.DAYS,
                id -> query().eq("badge_id", id).one());

        return badge == null ? ResultInfo.fail("徽章不存在") : ResultInfo.success(badge);
    }

    @Override
    public ResultInfo getAllBadges() {

        List<Badge> list = query().list();
        // 将徽章信息存入redis
        list.forEach(badge -> {
            String key = RedisConstant.BADGE_INFO + badge.getBadgeId();
            redisUtil.saveObjAsJson(key, badge, RedisConstant.BADGE_INFO_TTL, TimeUnit.DAYS);
        });

        return ResultInfo.success(list);
    }

    @Override
    public ResultInfo addBadge(Map<String, Object> map) {
//        取出badge中的各种信息，包括name，description，price，stock，
//        然后将其存入数据库中，同时将其存入redis中
        Badge badge = new Badge();
        BeanUtil.fillBeanWithMap(map, badge, true);

        Badge badgeName = query().eq("badge_name", badge.getBadgeName()).one();
        if (badgeName != null) {
            return ResultInfo.fail("徽章已存在");
        }
        save(badge);
        badgeName = query().eq("badge_name", badge.getBadgeName()).one();
        if (badgeName == null) {
            return ResultInfo.fail("徽章添加失败");
        }

        String key = RedisConstant.BADGE_INFO + badge.getBadgeId();
        redisUtil.saveObjAsJson(key, badge, RedisConstant.BADGE_INFO_TTL, TimeUnit.DAYS);
        return ResultInfo.success(badge.getBadgeId());
    }

    @Override
    public ResultInfo updateBadge(Map<String, Object> map) {
        Badge badge = new Badge();
        BeanUtil.fillBeanWithMap(map, badge, true);
        boolean isSuccess = updateById(badge);
        if (!isSuccess) {
            return ResultInfo.fail("徽章更新失败");
        }

        String key = RedisConstant.BADGE_INFO + badge.getBadgeId();
        redisTemplate.delete(key);
        return ResultInfo.success("徽章更新成功");

    }

    @Override
    public ResultInfo deleteBadgeById(String badgeId) {
        Badge badge = query().eq("badge_id", badgeId).one();
        if (badge == null) {
            return ResultInfo.fail("徽章不存在");
        }
        boolean isSuccess = removeById(badgeId);
        if (!isSuccess) {
            return ResultInfo.fail("徽章删除失败");
        }
        return ResultInfo.success("徽章删除成功");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @RedissonLock(prefixKey = RedisConstant.USER_ID_LOCK, key = "#user.getUserId()")
    public ResultInfo buyBadge(Long badgeId) {

        ResultInfo isOk = check(badgeId, false);
        if (isOk.getCode() != 200) {
            return isOk;
        }
        Map<String, Object> data = (Map<String, Object>) isOk.getData();
        Badge badge = (Badge) data.get("badge");
        Account userAccount = (Account) data.get("userAccount");
        UserDTO user = (UserDTO) data.get("user");
        // 扣除用户余额
        Long userId = user.getUserId();
        boolean isSuccess = accountService.update().set("user_money", userAccount.getUserMoney() - badge.getBadgeValue()).eq("user_id", userId).update();
        if (!isSuccess) {
            return ResultInfo.fail("余额扣除失败");
        }
        setUserBadgeService.save(new SetUserBadge(userId, badgeId));
        redisTemplate.opsForSet().add(RedisConstant.BADGE_OWNER + badgeId, String.valueOf(userId));
        Long orderId = idUtil.nextId("order");
        orderService.addOrder(new Order(orderId, userId, badgeId, new Date()));
        redisTemplate.delete(RedisConstant.ACCOUNT_INFO + userId);
        // 封装活动事件
        ActivityData activityData = ActivityData.builder().badgeId(badgeId)
                .badgeName(badge.getBadgeName())
                .build();
        Activity activity = Activity.builder()
                .type(ActivityEnum.GOODS_BUY.getActivityEventId())
                .targetId(userId).userId(userId)
                .createTime(new Date()).activityData(activityData).build();
        activityEventPublisher.publish(activity);
        return ResultInfo.success("购买成功");
    }

    private ResultInfo<?> check(Long badgeId, boolean isLimit) {
        // 查询用户信息
        UserDTO user = UserDTOHolder.get();
        if (user == null) {
            return ResultInfo.fail("用户未登录");
        }
        Account userAccount = accountService.query().eq("user_id", user.getUserId()).one();
        if (userAccount == null) {
            Account account = new Account();
            account.setUserId(user.getUserId());
            account.setUserMoney(10L);
            boolean isSaved = accountService.save(account);
            if (!isSaved) {
                return ResultInfo.fail("用户账户创建失败");
            }
            userAccount = account;
        }
        Badge badge = query().eq("badge_id", badgeId).one();
        if (badge == null) {
            return ResultInfo.fail("徽章不存在");
        }
        if ((isLimit && badge.getBadgeType() == 0) || (!isLimit && badge.getBadgeType() == 1)) {
            return ResultInfo.fail("参数设置错误，此徽章" + (isLimit ? "非" : "") + "限量徽章");
        }

        // 判断用户是否已经购买
        String key = RedisConstant.BADGE_OWNER + badgeId;
        boolean exists = Boolean.TRUE.equals(redisTemplate.hasKey(key));
        if (!exists) {
            List<SetUserBadge> list = setUserBadgeService.query().eq("badge_id", badgeId).list();
            List<String> collect = list.stream().map(s -> s.getUserId().toString()).collect(Collectors.toList());
            if (collect.size() > 0)
                redisTemplate.opsForSet().add(key, collect.toArray(new String[0]));
        }

        Boolean isMember = redisTemplate.opsForSet().isMember(RedisConstant.BADGE_OWNER + badgeId, user.getUserId().toString());
        if (Boolean.TRUE.equals(isMember)) {
            return ResultInfo.fail("已购买该徽章");
        }
        if (badge.getBadgeValue() > userAccount.getUserMoney()) {
            return ResultInfo.fail("余额不足");
        }
        return ResultInfo.success(Map.of("badge", badge, "userAccount", userAccount, "user", user));
    }

    @Override
    @RedissonLock(prefixKey = RedisConstant.USER_ID_LOCK, key = "#user.getUserId()")
    public ResultInfo buyLimitedBadge(Long badgeId) {

        ResultInfo isOk = check(Long.valueOf(badgeId), true);
        if (isOk.getCode() != 200) {
            return isOk;
        }

        Map<String, Object> data = (Map<String, Object>) isOk.getData();
        Badge badge = (Badge) data.get("badge");
        Account userAccount = (Account) data.get("userAccount");
        UserDTO user = (UserDTO) data.get("user");

        Boolean hasStockKey = redisTemplate.hasKey(RedisConstant.BADGE_STOCK + badgeId);
        if (Boolean.FALSE.equals(hasStockKey)) {
            redisTemplate.opsForValue().set(RedisConstant.BADGE_STOCK + badgeId, badge.getBadgeStock().toString());
        }
        // 使用lua脚本，保证原子性
        Long orderId = idUtil.nextId("order");
        Long resultNum;
        Long userId = user.getUserId();
        try {
            resultNum = redisTemplate.execute(luaScript, List.of(RedisConstant.BADGE_STOCK + badgeId), badgeId, userId.toString());
        } catch (Exception e) {
            log.error("调用lua脚本出错:'{}'", BUY_LUA_SCRIPT, e);
            return ResultInfo.fail("购买失败");
        }
        if (resultNum == null) {
            return ResultInfo.fail("购买失败");
        }
        if (resultNum != 2) {
            return ResultInfo.fail("库存不足");
        }
        // 扣减库存成功，orderId为订单号
        // 将订单号存入redis，设置过期时间，过期后将库存加回去
        // 扣除用户余额
        try {
            boolean isSuccess = accountService.update().set("user_money", userAccount.getUserMoney() - badge.getBadgeValue()).eq("user_id", userId).update();
            if (!isSuccess) {
                return ResultInfo.fail("余额扣除失败");
            }
            setUserBadgeService.save(new SetUserBadge(userId, badgeId));
            redisTemplate.opsForSet().add(RedisConstant.BADGE_OWNER + badgeId, String.valueOf(userId));
            orderService.addOrder(new Order(orderId, userId, badgeId, new Date()));
            update().eq("badge_id", badgeId).set("badge_stock", badge.getBadgeStock() - 1).update();
            redisTemplate.delete(RedisConstant.ACCOUNT_INFO + userId);
            // 封装活动事件
            ActivityData activityData = ActivityData.builder().badgeId(badgeId)
                    .badgeName(badge.getBadgeName())
                    .build();
            Activity activity = Activity.builder()
                    .type(ActivityEnum.GOODS_BUY.getActivityEventId())
                    .targetId(userId).userId(userId)
                    .createTime(new Date()).activityData(activityData).build();
            activityEventPublisher.publish(activity);
            return ResultInfo.success("购买成功");
        } catch (Exception e) {
            log.error("购买徽章失败，用户ID:{},徽章ID:{}", badgeId, userId, e);
            redisTemplate.opsForValue().increment(RedisConstant.BADGE_STOCK + badgeId);
        }
        return ResultInfo.fail("购买失败");


    }

    @Override
    public ResultInfo getUserBadges(Long userId) {
        List<SetUserBadge> list = setUserBadgeService.query().eq("user_id", userId).list();
        List<Long> list1 = list.stream().map(SetUserBadge::getBadgeId).toList();
        return ResultInfo.success(list1);
    }
}




