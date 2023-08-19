package com.chen.LeoBlog.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chen.LeoBlog.base.ResultInfo;
import com.chen.LeoBlog.constant.RedisConstant;
import com.chen.LeoBlog.exception.CommonErrorEnum;
import com.chen.LeoBlog.mapper.AccountMapper;
import com.chen.LeoBlog.po.Account;
import com.chen.LeoBlog.service.AccountService;
import com.chen.LeoBlog.service.UserService;
import com.chen.LeoBlog.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author rtg19
 * @description 针对表【lb_account】的数据库操作Service实现
 * @createDate 2023-06-11 18:28:49
 */
@Service
@Slf4j
public class AccountServiceImpl extends ServiceImpl<AccountMapper, Account>
        implements AccountService {

    @Resource
    private RedisUtil redisUtil;

    @Resource
    private UserService userService;


    @Override
    public ResultInfo getAccountByUserId(Long userId) {
        Account account = getAccount(userId);
        if (account == null) {
            if (userService.getUserObj(userId) == null) {
                return ResultInfo.fail("用户不存在");

            } else {
                Account build = Account.builder().userMoney(10L).userId(userId).updateTime(new Date()).build();
                if (!save(build)) {
                    return ResultInfo.fail(CommonErrorEnum.SYSTEM_ERROR);
                }
            }
        }
        return ResultInfo.success(account);
    }

    @Override
    public Account getAccount(Long userId) {
        String key = RedisConstant.ACCOUNT_INFO + userId;
        return redisUtil.getObjWithCache(key, userId, Account.class, RedisConstant.ACCOUNT_INFO_TTL, TimeUnit.DAYS,
                id -> query().eq("user_id", id).one());
    }

    @Override
    public ResultInfo addAccount(Account account) {
        boolean isSuccess = save(account);
        if (!isSuccess) {
            return ResultInfo.fail("添加失败");
        }
        Account ac = query().eq("user_id", account.getUserId()).one();
        return ResultInfo.success(ac);
    }

    @Override
    public ResultInfo updateAccount(Account account) {
        return null;
    }

    @Override
    public ResultInfo getAllAccounts() {
        return null;
    }
}




