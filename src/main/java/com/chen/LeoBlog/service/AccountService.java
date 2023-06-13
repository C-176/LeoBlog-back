package com.chen.LeoBlog.service;

import com.chen.LeoBlog.base.ResultInfo;
import com.chen.LeoBlog.po.Account;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author rtg19
* @description 针对表【lb_account】的数据库操作Service
* @createDate 2023-06-11 18:28:49
*/
public interface AccountService extends IService<Account> {

    ResultInfo getAccountByUserId(Long userId);

    Account getAccount(Long userId);

    ResultInfo addAccount(Account account);

    ResultInfo updateAccount(Account account);

    ResultInfo getAllAccounts();
}
